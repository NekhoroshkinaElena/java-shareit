package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;

public class BookingMapper {

    public static BookingDtoOutput toBookingDtoOutput(Booking booking) {
        return new BookingDtoOutput(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                booking.getBooker(),
                ItemMapper.toItemDtoForBooking(booking.getItem()));
    }

    public static Booking toBooking(BookingDtoInput bookingDtoInput) {
        return new Booking(
                bookingDtoInput.getStart(),
                bookingDtoInput.getEnd(),
                BookingStatus.WAITING);
    }

    public static BookingDtoForItem toBookingDtoForItem(Booking booking) {
        return new BookingDtoForItem(
                booking.getId(),
                booking.getBooker().getId());
    }
}
