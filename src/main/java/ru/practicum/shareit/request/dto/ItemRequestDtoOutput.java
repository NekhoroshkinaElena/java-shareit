package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemRequestDtoOutput {
    long id;
    String description;
    LocalDateTime created;
    List<ItemDto> items;

    public ItemRequestDtoOutput(long id, String description, LocalDateTime localDateTime) {
        this.id = id;
        this.description = description;
        this.created = localDateTime;
    }
}
