package ru.practicum.shareit.booking.dto;

import lombok.Value;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Value
public class BookingDtoInput {
    long itemId;
    LocalDateTime start;
    LocalDateTime end;
}
