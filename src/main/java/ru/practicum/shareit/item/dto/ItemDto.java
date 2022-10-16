package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;

    public ItemDto(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
