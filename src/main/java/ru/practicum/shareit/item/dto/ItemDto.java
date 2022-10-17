package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
public class ItemDto {
    private long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NotNull
    private Boolean available;
    private long requestId;

    public ItemDto(long id, String name, String description, Boolean available, long itemRequest) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = itemRequest;
    }
}
