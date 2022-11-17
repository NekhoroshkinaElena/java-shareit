package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@Transactional
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {

    private final EntityManager em;
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    private User owner = new User(1L,
            "owner",
            "emailOwner@ya.ru");

    private Item item = new Item(
            1L,
            "item",
            "description",
            true,
            owner,
            null);

    private Item item2 = new Item(
            2L,
            "item2",
            "description2",
            true,
            owner,
            null);

    private User booker = new User(2L,
            "booker",
            "emailBooker@ya.ru");

    private BookingDtoInput bookingDtoInput = new BookingDtoInput(1L,
            LocalDateTime.of(2022, 11, 20, 12, 0, 0),
            LocalDateTime.of(2022, 11, 25, 12, 0, 0));

    private BookingDtoInput bookingDtoInput2 = new BookingDtoInput(2L,
            LocalDateTime.of(2022, 11, 20, 12, 0, 0),
            LocalDateTime.of(2022, 11, 25, 12, 0, 0));

    @BeforeEach
    void createBeforeEach() {
        userService.add(UserMapper.toUserDto(owner));
        itemService.create(ItemMapper.toItemDto(item), owner.getId());
        itemService.create(ItemMapper.toItemDto(item2), owner.getId());
        userService.add(UserMapper.toUserDto(booker));
    }

    @Test
    public void saveBooking() {
        BookingDtoOutput booking = bookingService.save(bookingDtoInput, booker.getId());

        TypedQuery<Booking> query = em.createQuery(
                "select b from Booking b where b.id = : id", Booking.class);
        Booking booking1 = query.setParameter("id", booking.getId())
                .getSingleResult();

        assertThat(booking1.getId(), notNullValue());
        assertThat(booking1.getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(booking1.getItem().getId(), equalTo(item.getId()));
    }

    @Test
    public void approveBooking() {
        BookingDtoOutput bookingDtoOutput = bookingService.save(bookingDtoInput, booker.getId());
        bookingService.approve(owner.getId(), bookingDtoOutput.getId(), true);
        TypedQuery<Booking> query = em.createQuery(
                "select b from Booking b where b.id = : id", Booking.class);
        Booking booking1 = query.setParameter("id", bookingDtoOutput.getId())
                .getSingleResult();

        assertThat(booking1.getId(), notNullValue());
        assertThat(booking1.getStatus(), equalTo(BookingStatus.APPROVED));
        assertThat(booking1.getItem().getId(), equalTo(item.getId()));
    }

    @Test
    public void getById() {
        bookingService.save(bookingDtoInput, booker.getId());
        BookingDtoOutput bookingGetId = bookingService.getById(1L, 1L);

        TypedQuery<Booking> query = em.createQuery(
                "select b from Booking b where b.id = : id", Booking.class);
        Booking booking1 = query.setParameter("id", bookingGetId.getId())
                .getSingleResult();

        assertThat(booking1.getId(), notNullValue());
        assertThat(booking1.getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(booking1.getItem().getId(), equalTo(item.getId()));
        assertThat(booking1.getBooker().getId(), equalTo(booker.getId()));
    }

    @Test
    public void findAllForBooker() {
        BookingDtoOutput bookingDtoOutput1 = bookingService.save(bookingDtoInput, booker.getId());
        BookingDtoOutput bookingDtoOutput2 = bookingService.save(bookingDtoInput2, booker.getId());
        List<BookingDtoOutput> bookings = bookingService.findAllForBooker(
                0, 10, booker.getId(), "ALL");

        assertThat(bookings.size(), equalTo(2));
        assertTrue(bookings.contains(bookingDtoOutput1));
        assertTrue(bookings.contains(bookingDtoOutput2));
    }

    @Test
    public void findAllForOwner() {
        BookingDtoOutput bookingDtoOutput1 = bookingService.save(bookingDtoInput, booker.getId());
        BookingDtoOutput bookingDtoOutput2 = bookingService.save(bookingDtoInput2, booker.getId());
        List<BookingDtoOutput> bookings = bookingService.findAllForOwner(
                0, 10, owner.getId(), "ALL");

        assertTrue(bookings.contains(bookingDtoOutput1));
        assertTrue(bookings.contains(bookingDtoOutput2));
    }
}
