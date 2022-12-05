package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;

import java.util.List;

public interface BookingService {

    BookingDtoOutput save(BookingDtoInput bookingDtoInput, long userId);

    BookingDtoOutput approve(long userId, long bookingId, boolean status);

    BookingDtoOutput getById(long bookingId, long userId);

    List<BookingDtoOutput> findAllForBooker(int from, int size, long bookerId, String state);

    List<BookingDtoOutput> findAllForOwner(int from, int size, long ownerId, String state);
}
