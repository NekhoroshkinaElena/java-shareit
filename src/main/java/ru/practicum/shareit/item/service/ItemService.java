package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto getItemById(long id);

    List<Item> getItems(long userId);

    ItemDto createItem(Item item, long id);

    Item itemUpdate(Item item, long id, long userId);

    List<Item> searchItems(String text);
}
