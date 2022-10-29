package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto item, long userId);

    ItemDto getById(long id);

    List<ItemDto> getAll(long userId);

    ItemDto update(ItemDto itemDto, long id, long userId);

    List<ItemDto> search(String text);
}
