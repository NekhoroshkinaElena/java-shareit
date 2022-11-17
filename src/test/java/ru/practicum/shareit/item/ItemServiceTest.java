package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDtoInput;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    User user = new User(1L, "user", "userEmail@ua.ru");
    ItemDto itemDto = new ItemDto(1L, "item", "desc", true, 0);

    Item item = new Item(1L, "item", "desc", true, user, null);
    Item item2 = new Item(2L, "item2", "desc2", true, user, null);

    Item itemUpdate = new Item(1L, "itemUpdate", "descUpdate", true, user, null);
    ItemRequest itemRequest = new ItemRequest("description");

    Booking booking = new Booking(LocalDateTime.now(), LocalDateTime.now(), BookingStatus.WAITING);

    Comment comment = new Comment("comment", LocalDateTime.now());

    CommentDtoInput commentDtoInput = new CommentDtoInput();

    @Test
    public void createItemWithNotFoundUser() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.create(itemDto, user.getId()));
        assertEquals("Пользователь c идентификатором " + user.getId() + " не найден.", exception.getMessage());
    }

    @Test
    public void createItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any())).thenReturn(ItemMapper.toItem(itemDto));

        ItemDto itemDtoSave = itemService.create(itemDto, user.getId());
        assertEquals(itemDtoSave.getId(), itemDto.getId());
    }

    @Test
    public void ItemUpdateWithNotFoundUser() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.update(itemDto, itemDto.getId(), user.getId()));
        assertEquals("Пользователя c идентификатором " + user.getId() + " не существует.", exception.getMessage());
    }

    @Test
    public void ItemUpdateWithNotFoundItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.update(itemDto, item.getId(), user.getId()));
        assertEquals("Вещь с id " + item.getId() + " не найдена.", exception.getMessage());
    }

    @Test
    public void ItemUpdateWithWrongUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.update(itemDto, item.getId(), 2L));
        assertEquals("Обновить информацию о вещи может только её владелец.", exception.getMessage());
    }

    @Test
    public void ItemUpdate() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(itemUpdate);

        ItemDto itemGet = itemService.update(itemDto, item.getId(), user.getId());

        assertEquals(itemGet.getId(), itemUpdate.getId());
        assertEquals(itemGet.getName(), itemUpdate.getName());
    }

    @Test
    public void getByIdWithNotFoundItem() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.getById(item.getId(), user.getId()));
        assertEquals("Вещь с id " + item.getId() + " не найдена.", exception.getMessage());
    }

    @Test
    public void getItemById() {
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);

        comment.setAuthor(user);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(bookingRepository.findByItemIdAndEndBefore(anyLong(), any())).thenReturn(booking);

        when(commentRepository.getAllByItemId(anyLong())).thenReturn(List.of(comment));

        ItemOutputDto itemGet = itemService.getById(itemDto.getId(), user.getId());
    }

    @Test
    public void getAllWithNotFoundUser() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.getAll(user.getId(), 0, 20));
        assertEquals("Пользователя c идентификатором " + user.getId() + " не существует.",
                exception.getMessage());
    }

    @Test
    public void searchWithEmptyText() {
        List<ItemDto> items = itemService.search("", 0, 20);
        assertEquals(items.size(), 0);
    }

    @Test
    public void search() {
        when(itemRepository.search(any(), any())).thenReturn(List.of(item, item2));
        List<ItemDto> items = itemService.search("ite", 0, 20);
        assertEquals(items.size(), 2);
    }

    @Test
    public void addCommentWithWrongBooking() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.addComment(item.getId(), user.getId(), new CommentDtoInput()));
        assertEquals("Отзыв может оставить только тот пользователь, " +
                        "который брал эту вещь в аренду, и только после окончания срока аренды.",
                exception.getMessage());
    }

    @Test
    public void addCommentWithEmptyComment() {
        commentDtoInput.setText("");
        when(bookingRepository.findBookingByItemIdAndBookerIdAndEndBefore(
                anyLong(), anyLong(), any())).thenReturn(booking);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.addComment(item.getId(), user.getId(), commentDtoInput));
        assertEquals("Комментарий не может быть пустым.",
                exception.getMessage());
    }
}