package ru.practicum.shareit.booking.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Value
public class BookingDtoInput {
    @NotBlank
    @NotNull
    long itemId;
    @NotNull
    LocalDateTime start;
    @NotNull
    LocalDateTime end;
}
