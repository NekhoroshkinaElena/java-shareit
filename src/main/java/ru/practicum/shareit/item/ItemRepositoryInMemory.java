package ru.practicum.shareit.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepositoryInMemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Getter
@Setter
@RequiredArgsConstructor
@Slf4j
public class ItemRepositoryInMemory {
    private final UserRepositoryInMemory repository;
    private final Map<Long, Item> items = new HashMap<>();
    private Map<Long, ItemDto> itemsDto = new HashMap<>();
    private final Map<Long, Long> users = new HashMap<>(); //key - id вещи, values - id пользователя
    private long id = 1;

    public long getIdItem() {
        return id++;
    }

    public ItemDto getItem(long id) {
        ItemDto itemDto = ItemMapper.toItemDto(items.get(id));
        itemDto.setId(id);
        return itemDto;
    }

    public List<Item> getItems(long userId) {
        List<Long> idItems = new ArrayList<>();
        for (Long u : users.keySet()) {
            if (users.get(u) == userId) {
                idItems.add(u);
            }
        }
        List<Item> itemList = new ArrayList<>();
        for (long i : items.keySet()) {
            if (idItems.contains(i)) {
                itemList.add(items.get(i));
            }
        }
        return itemList;
    }

    public ItemDto createItem(Item item, long userId) {
        if (repository.getUser(userId) == null) {
            log.error("пользователя не существует");
            throw new ValidationException("пользователя не существует");
        }
        Item itemNew = new Item(getIdItem(), item.getName(), item.getDescription(), item.getAvailable(),
                item.getOwner(), item.getRequest());
        if (itemsDto.containsValue(ItemMapper.toItemDto(item))) {
            log.error("такая вещь уже существует");
            throw new RuntimeException("такая вещь уже существует");
        }
        users.put(itemNew.getId(), userId);
        ItemDto itemDto = ItemMapper.toItemDto(itemNew);
        itemDto.setId(itemNew.getId());
        itemsDto.put(id, itemDto);
        items.put(itemNew.getId(), itemNew);
        return itemDto;
    }

    public Item itemUpdate(Item item, long id, long userId) {
        if (repository.getUser(userId) == null) {
            log.error("пользователя не существует");
            throw new ValidationException("пользователя не существует");
        }
        if (users.get(id) == null || users.get(id) != userId) {
            log.error("неверный пользователь");
            throw new ValidationException("неверный пользователь");
        }
        itemsDto.remove(id);
        Item itemUpdate = items.get(id);
        if (item.getName() != null) {
            itemUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemUpdate.setAvailable(item.getAvailable());
        }
        itemsDto.put(id, ItemMapper.toItemDto(itemUpdate));
        return itemUpdate;
    }

    public List<Item> searchItems(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        String textR = text.toLowerCase();
        List<Item> itemList = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getDescription().trim().toLowerCase().contains(textR) ||
                    item.getName().trim().toLowerCase().contains(textR)) {
                if (item.getAvailable()) {
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }
}
