package ru.practicum.shareit.booking.dto;

import lombok.Value;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Value
public class BookingDtoOutput {
    long id;
    LocalDateTime start;
    LocalDateTime end;
    BookingStatus status;
    User booker;
    ItemDtoForBooking item;
}
