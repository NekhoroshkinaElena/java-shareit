package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentDtoOutput;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemDto create(ItemDto itemDto, long userId) {
        Item item = ItemMapper.toItem(itemDto);
        throwIfUserNotFound(userId);
        item.setOwner(userRepository.findById(userId).get());
        if (item.getRequest() != null) {
            itemRequestRepository.save(item.getRequest());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    public ItemDto update(ItemDto itemDto, long id, long userId) {
        Item item = ItemMapper.toItem(itemDto);
        throwIfUserNotFound(userId);
        throwIfItemNotFound(id);
        if (itemRepository.findById(id).get().getOwner().getId() != userId || userId == 0) {
            log.error("неверный пользователь");
            throw new NotFoundException("неверный пользователь");
        }
        Item itemUpdate = itemRepository.findById(id).get();
        if (item.getAvailable() != null) {
            itemUpdate.setAvailable(item.getAvailable());
        }
        if (item.getDescription() != null) {
            itemUpdate.setDescription(item.getDescription());
        }
        if (item.getName() != null) {
            itemUpdate.setName(item.getName());
        }
        return ItemMapper.toItemDto(itemRepository.save(itemUpdate));
    }

    @Override
    public ItemOutputDto getById(long itemId, long ownerId) {//ЕСЛИ НЕТ БРОНИРОВАНИЙ ВОЗВРАЩАЕМ NULL!!!!
        throwIfItemNotFound(itemId);
        Item item = itemRepository.findById(itemId).get();
        ItemOutputDto itemOutputDto = ItemMapper.toItemDtoOutput(item);

        if (itemRepository.findById(itemId).get().getOwner().getId() == ownerId) {
            BookingDtoForItem bookingDtoLast = new BookingDtoForItem();
            BookingDtoForItem bookingDtoFuture = new BookingDtoForItem();
            Booking bookingLast = bookingRepository.findByItem_IdAndEndBefore(itemId, LocalDateTime.now());
            Booking bookingFuture = bookingRepository.findFirstByItem_IdAndStartAfter(itemId, LocalDateTime.now());

            if (bookingLast != null) {
                bookingDtoLast.setBookerId(bookingLast.getBooker().getId());
                bookingDtoLast.setId(bookingLast.getId());
                itemOutputDto.setLastBooking(bookingDtoLast);
            }
            if (bookingFuture != null) {
                bookingDtoFuture.setBookerId(bookingFuture.getBooker().getId());
                bookingDtoFuture.setId(bookingFuture.getId());
                itemOutputDto.setNextBooking(bookingDtoFuture);
            }
        }
        itemOutputDto.setComments(commentRepository.getAllByItem_Id(itemId).stream()
                .map(CommentMapper::commentDtoOutput).collect(Collectors.toList()));
        return itemOutputDto;
    }

    public List<ItemOutputDto> getAll(long userId) {
        throwIfUserNotFound(userId);
        return itemRepository.findAll().stream()
                .filter(item -> item.getOwner().getId() == userId).map(item -> getById(item.getId(),
                        item.getOwner().getId()))
                .collect(Collectors.toList());
    }

    public List<ItemDto> search(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public CommentDtoOutput addComment(long itemId, long userId, CommentDto commentDto) {
        Booking booking = bookingRepository.findBookingByItem_IdAndBooker_IdAndEndBefore(
                itemId, userId, LocalDateTime.now());
        if (booking == null) {
            log.error("Вы не можете оставить комментарий");
            throw new ValidationException("Вы не можете оставить комментарий");
        }
        if (commentDto.getText().isEmpty() || commentDto.getText().isBlank()) {
            log.error("комментарий не может быть пустым");
            throw new ValidationException("комментарий не может быть пустым");
        }
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(itemRepository.findById(itemId).get());
        comment.setAuthor(userRepository.findById(userId).get());
        comment.setItem(itemRepository.findById(itemId).get());
        comment.setAuthor(userRepository.findById(userId).get());
        return CommentMapper.commentDtoOutput(commentRepository.save(comment));
    }

    public void throwIfUserNotFound(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.error("пользователя c идентификатором " + userId + " не существует");
            throw new NotFoundException("пользователя c идентификатором " + userId + " не существует");
        }
    }

    public void throwIfItemNotFound(long itemId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            log.error("вещи c идентификатором " + itemId + " не существует");
            throw new NotFoundException("вещи c идентификатором " + itemId + " не существует");
        }
    }
}
