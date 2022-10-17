package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item create(Item item, long userId);

    Item getById(long id);

    List<Item> getAll(long userId);

    Item update(Item item, long id, long userId);

    List<Item> search(String text);
}
