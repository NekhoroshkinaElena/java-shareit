package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingDtoOutput {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private User booker;
    private ItemDtoForBooking item;

    public BookingDtoOutput(long id, LocalDateTime start, LocalDateTime end,
                            BookingStatus status, User booker, ItemDtoForBooking item) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.status = status;
        this.booker = booker;
        this.item = item;
    }
}
