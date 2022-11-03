package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;

import java.util.List;

public interface BookingService {

    BookingDtoOutput save(BookingDtoInput bookingDtoInput, long userId);

    BookingDtoOutput approve(long userId, long bookingId, boolean status);

    BookingDtoOutput getById(long bookingId, long userId);

    List<BookingDtoOutput> findAll(long bookerId, String state);

    List<BookingDtoOutput> findAllOwner(long ownerId, String state);
}