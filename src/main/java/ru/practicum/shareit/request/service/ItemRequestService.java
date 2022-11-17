package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoOutput add(ItemRequestDtoInput itemRequestDto, long userId);

    List<ItemRequestDtoOutput> findAllOwner(long userId);

    List<ItemRequestDtoOutput> findAll(int from, int size, long userId);

    ItemRequestDtoOutput findById(long requestId, long userId);
}
