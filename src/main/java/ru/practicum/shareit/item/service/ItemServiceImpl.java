package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemRepositoryInMemory;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepositoryInMemory inMemory;

    public ItemDto getItemById(long id) {
        return inMemory.getItem(id);
    }

    public List<Item> getItems(long userId) {
        return inMemory.getItems(userId);
    }

    public ItemDto createItem(Item item, long id) {
        return inMemory.createItem(item, id);
    }

    public Item itemUpdate(Item item, long id, long userId) {
        return inMemory.itemUpdate(item, id, userId);
    }

    public List<Item> searchItems(String text) {
        return inMemory.searchItems(text);
    }
}
