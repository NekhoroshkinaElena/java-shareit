package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;
import ru.practicum.shareit.request.model.ItemRequest;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDtoInput itemRequestDtoInput) {
        return new ItemRequest(itemRequestDtoInput.getDescription());
    }

    public static ItemRequestDtoOutput toItemRequestDtoOutput(ItemRequest itemRequest) {
        return new ItemRequestDtoOutput(itemRequest.getId(), itemRequest.getDescription(),
                itemRequest.getCreated());
    }
}
