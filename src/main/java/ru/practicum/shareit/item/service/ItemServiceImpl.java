package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepositoryInMemory;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepositoryInMemory;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepositoryInMemory itemRepositoryInMemory;
    private final UserRepositoryInMemory userRepositoryInMemory;

    public Item create(Item item, long userId) {
        throwIfUserNotFound(userId);
        item.setOwner(userRepositoryInMemory.getById(userId));
        return itemRepositoryInMemory.create(item);
    }

    public Item update(Item item, long id, long userId) {
        throwIfUserNotFound(userId);
        if (itemRepositoryInMemory.getById(id).getOwner().getId() != userId) {
            log.error("неверный пользователь");
            throw new NotFoundException("неверный пользователь");
        }
        return itemRepositoryInMemory.update(item, id);
    }

    public Item getById(long id) {
        return itemRepositoryInMemory.getById(id);
    }

    public List<Item> getAll(long userId) {
        throwIfUserNotFound(userId);
        return itemRepositoryInMemory.getAll().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    public List<Item> search(String text) {
        return itemRepositoryInMemory.search(text);
    }

    public void throwIfUserNotFound(long userId) {
        if (userRepositoryInMemory.getById(userId) == null) {
            log.error("пользователя c идентификатором " + userId + " не существует");
            throw new NotFoundException("пользователя c идентификатором " + userId + " не существует");
        }
    }
}
