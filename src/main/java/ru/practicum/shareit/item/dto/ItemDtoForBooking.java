package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemDtoForBooking {
    private Long id;
    private String name;

    public ItemDtoForBooking(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
