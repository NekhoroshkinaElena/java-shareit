package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.item.comment.CommentDtoOutput;

import java.util.List;

@Getter
@Setter
public class ItemOutputDto {
    Long id;
    String name;
    String description;
    Boolean available;
    BookingDtoForItem lastBooking;
    BookingDtoForItem nextBooking;
    List<CommentDtoOutput> comments;

    public ItemOutputDto(Long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
