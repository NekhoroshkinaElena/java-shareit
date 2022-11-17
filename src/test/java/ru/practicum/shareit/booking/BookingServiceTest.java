package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    User user = new User(1L, "name", "email@ya.ru");

    User booker = new User(2L, "booker", "emailBooker@ya.ru");

    Booking booking = new Booking(LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(5),
            BookingStatus.WAITING);

    Booking booking2 = new Booking(LocalDateTime.now().plusDays(2),
            LocalDateTime.now().plusDays(4),
            BookingStatus.WAITING);

    Item item = new Item(1L, "item", "desc", true, user, null);
    Item item2 = new Item(2L, "item2", "desc2", true, user, null);

    @Test
    public void saveBookingWithWrongUser() {
        when(itemRepository.findById(anyLong())).thenReturn(
                Optional.of(new Item(1L, "name", "desc", true,
                        new User(1L, "name", "email@ya.ru"), new ItemRequest("desc"))));

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                bookingService.save(new BookingDtoInput(1L,
                        LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(3)), 1L));
        assertEquals("Владелец вещи не может её забронировать.", notFoundException.getMessage());
    }

    @Test
    public void saveBookingWithWrongTime() {
        when(itemRepository.findById(anyLong())).thenReturn(
                Optional.of(new Item(1L, "name", "desc", true,
                        new User(1L, "name", "email@ya.ru"), new ItemRequest("desc"))));

        ValidationException validationException = assertThrows(ValidationException.class, () ->
                bookingService.save(new BookingDtoInput(1L,
                        LocalDateTime.now(),
                        LocalDateTime.now().minusDays(1)), 2L));
        assertEquals("Установите корректное время для бронирования.", validationException.getMessage());
    }

    @Test
    public void saveBookingWithNotExistUser() {
        when(itemRepository.findById(anyLong())).thenReturn(
                Optional.of(new Item(1L, "name", "desc", true,
                        new User(1L, "name", "email@ya.ru"), new ItemRequest("desc"))));
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                bookingService.save(new BookingDtoInput(1L,
                        LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(2)), 2L));
        assertEquals("Пользователя c идентификатором " + 2L + " не существует.",
                notFoundException.getMessage());
    }

    @Test
    public void saveBookingWhenItemAlreadyBusy() {
        when(itemRepository.findById(anyLong())).thenReturn(
                Optional.of(new Item(1L, "name", "desc", false,
                        new User(1L, "name", "email@ya.ru"), new ItemRequest("desc"))));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User(1L, "name", "emal@ya.ru")));
        ValidationException validationException = assertThrows(ValidationException.class, () ->
                bookingService.save(new BookingDtoInput(1L,
                        LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(2)), 2L));
        assertEquals("Вещь занята другим пользователем.",
                validationException.getMessage());
    }

    @Test
    public void approveWithNotFoundUser() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                bookingService.approve(1L, 1L, true));
        assertEquals("пользователя c идентификатором " + 1 + " не существует.", notFoundException.getMessage());
    }

    @Test
    public void approveWithWrongBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                bookingService.approve(1L, 1L, true));
        assertEquals("Такого бронирования не существует.", notFoundException.getMessage());
    }

    @Test
    public void approveWithWrongUser() {
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                bookingService.approve(2L, 1L, true));
        assertEquals("Подтверждение или отклонение запроса на бронирование может быть выполнено " +
                "только владельцем вещи.", notFoundException.getMessage());
    }

    @Test
    public void approveWithAlreadyApproveStatus() {
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        ValidationException validationException = assertThrows(ValidationException.class, () ->
                bookingService.approve(1L, 1L, true));
        assertEquals("Статус бронирования уже подтверждён.", validationException.getMessage());
    }

    @Test
    public void approveBooking() {
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDtoOutput booking1 = bookingService.approve(1L, 1L, true);

        assertEquals(booking1.getId(), booking.getId());
        assertEquals(booking1.getStart(), booking.getStart());
        assertEquals(booking1.getEnd(), booking.getEnd());
        assertEquals(booking1.getStatus(), booking.getStatus());
    }

    @Test
    public void getByIdWithNotFoundUser() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                bookingService.getById(1L, 1L));
        assertEquals("пользователя c идентификатором " + 1 + " не существует.", notFoundException.getMessage());
    }

    @Test
    public void getNotFoundBooking() {
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                bookingService.getById(3L, 3L));
        assertEquals("Получить данные о бронировании может либо автор бронирования" +
                " либо владелец вещи.", notFoundException.getMessage());
    }

    @Test
    public void getBookingById() {
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        BookingDtoOutput booking1 = bookingService.getById(3L, 1L);
        assertEquals(booking1.getId(), booking.getId());
        assertEquals(booking1.getStart(), booking.getStart());
        assertEquals(booking1.getEnd(), booking.getEnd());
        assertEquals(booking1.getStatus(), booking.getStatus());
    }

    @Test
    public void findAllForBookingWithNotFoundUser() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                bookingService.findAllForBooker(0, 20, booker.getId(), "ALL"));
        assertEquals("пользователя c идентификатором " + booker.getId() + " не существует.",
                notFoundException.getMessage());
    }

    @Test
    public void findAllForBookingWithWrongParams() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        ValidationException validationException = assertThrows(ValidationException.class, () ->
                bookingService.findAllForBooker(-1, -1, booker.getId(), "ALL"));
        assertEquals("параметры не могут быть отрицательными", validationException.getMessage());
    }

    @Test
    public void findAllForBookingWithEmptyParams() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        ValidationException validationException = assertThrows(ValidationException.class, () ->
                bookingService.findAllForBooker(0, 0, booker.getId(), "ALL"));
        assertEquals("параметры не могут быть пустыми", validationException.getMessage());
    }

    @Test
    public void findAllForOwnerWithWrongUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(new ArrayList<>());

        ValidationException validationException = assertThrows(ValidationException.class, () ->
                bookingService.findAllForOwner(0, 20, booker.getId(), "ALL"));
        assertEquals("Вы не можете получить список бронирований, так как у вас нет вещей.",
                validationException.getMessage());
    }

    @Test
    public void findAllForOwner() {
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        booking2.setId(2L);
        booking2.setItem(item2);
        booking2.setBooker(booker);
        booking2.setStatus(BookingStatus.WAITING);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn((List.of(item, item2)));

        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(booking2, booking));

        List<BookingDtoOutput> bookings = bookingService.findAllForOwner(0, 20, user.getId(), "ALL");

        assertEquals(bookings.size(), 2);
    }

    @Test
    public void findAllOwnerWithUnknownState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn((List.of(item, item2)));

        ValidationException validationException = assertThrows(ValidationException.class, () ->
                bookingService.findAllForOwner(0, 20, booker.getId(), "UNKNOWN"));
        assertEquals("Unknown state: UNSUPPORTED_STATUS",
                validationException.getMessage());
    }

    @Test
    public void bookingMapperTest() {
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        BookingDtoForItem bookingDtoForItem = BookingMapper.toBookingDtoForItem(booking);
        assertEquals(bookingDtoForItem.getId(), booking.getId());
        assertEquals(bookingDtoForItem.getBookerId(), booking.getBooker().getId());
    }
}
