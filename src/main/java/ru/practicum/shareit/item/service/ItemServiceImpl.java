package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
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

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(getUser(userId));
        item.setRequest(itemRequestRepository.findById(itemDto.getRequestId()).orElse(null));
        if (item.getRequest() != null) {
            itemRequestRepository.save(item.getRequest());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, long itemId, long userId) {
        Item item = ItemMapper.toItem(itemDto);
        throwIfUserNotFound(userId);
        if (getItem(itemId).getOwner().getId() != userId || userId == 0) {
            log.error("Обновить информацию о вещи может только её владелец.");
            throw new NotFoundException("Обновить информацию о вещи может только её владелец.");
        }
        Item itemUpdate = getItem(itemId);
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
    public ItemOutputDto getById(long itemId, long ownerId) {
        Item item = getItem(itemId);
        ItemOutputDto itemOutputDto = ItemMapper.toItemDtoOutput(item);
        if (item.getOwner().getId() == ownerId) {
            Booking bookingLast = bookingRepository.findByItemIdAndEndBefore(itemId, LocalDateTime.now());
            Booking bookingFuture = bookingRepository.findFirstByItemIdAndStartAfter(itemId, LocalDateTime.now());
            if (bookingLast != null) {
                itemOutputDto.setLastBooking(BookingMapper.toBookingDtoForItem(bookingLast));
            }
            if (bookingFuture != null) {
                itemOutputDto.setNextBooking(BookingMapper.toBookingDtoForItem(bookingFuture));
            }
        }
        itemOutputDto.setComments(commentRepository.getAllByItemId(itemId).stream()
                .map(CommentMapper::commentDtoOutput).collect(Collectors.toList()));
        return itemOutputDto;
    }

    @Override
    public List<ItemOutputDto> getAll(long userId, int from, int size) {
        throwIfUserNotFound(userId);
        return itemRepository.findAll(PageRequest.of(from / size, size)).stream()
                .filter(item -> item.getOwner().getId() == userId).map(item -> getById(item.getId(),
                        item.getOwner().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text, int from, int size) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text, PageRequest.of(from / size, size))
                .stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public CommentDtoOutput addComment(long itemId, long userId, CommentDtoInput commentDtoInput) {
        Booking booking = bookingRepository.findBookingByItemIdAndBookerIdAndEndBefore(
                itemId, userId, LocalDateTime.now());
        if (booking == null) {
            log.error("Отзыв может оставить только тот пользователь, " +
                    "который брал эту вещь в аренду, и только после окончания срока аренды.");
            throw new ValidationException("Отзыв может оставить только тот пользователь, " +
                    "который брал эту вещь в аренду, и только после окончания срока аренды.");
        }
        if (commentDtoInput.getText().isEmpty() || commentDtoInput.getText().isBlank()) {
            log.error("Комментарий не может быть пустым.");
            throw new ValidationException("Комментарий не может быть пустым.");
        }
        Comment comment = CommentMapper.toComment(commentDtoInput);
        comment.setItem(getItem(itemId));
        comment.setAuthor(getUser(userId));
        return CommentMapper.commentDtoOutput(commentRepository.save(comment));
    }

    public void throwIfUserNotFound(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.error("Пользователя c идентификатором " + userId + " не существует.");
            throw new NotFoundException("Пользователя c идентификатором " + userId + " не существует.");
        }
    }

    private Item getItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Вещь с id " + itemId + " не найдена.");
                    return new NotFoundException("Вещь с id " + itemId + " не найдена.");
                });
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователя c идентификатором " + userId + " не найден.");
                    return new NotFoundException("Пользователя c идентификатором " + userId + " не найден.");
                });
    }
}
