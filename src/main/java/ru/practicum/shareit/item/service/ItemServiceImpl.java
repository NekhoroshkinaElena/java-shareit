package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepositoryInMemory;
import ru.practicum.shareit.item.dto.ItemDto;
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

    public ItemDto create(ItemDto itemDto, long userId) {
        Item item = ItemMapper.toItem(itemDto);
        throwIfUserNotFound(userId);
        item.setOwner(userRepositoryInMemory.getById(userId));
        return ItemMapper.toItemDto(itemRepositoryInMemory.create(item));
    }

    public ItemDto update(ItemDto itemDto, long id, long userId) {
        Item item = ItemMapper.toItem(itemDto);
        throwIfUserNotFound(userId);
        if (itemRepositoryInMemory.getById(id).getOwner().getId() != userId) {
            log.error("неверный пользователь");
            throw new NotFoundException("неверный пользователь");
        }
        return ItemMapper.toItemDto(itemRepositoryInMemory.update(item, id));
    }

    public ItemDto getById(long id) {
        return ItemMapper.toItemDto(itemRepositoryInMemory.getById(id));
    }

    public List<ItemDto> getAll(long userId) {
        throwIfUserNotFound(userId);
        return itemRepositoryInMemory.getAll().stream()
                .filter(item -> item.getOwner().getId() == userId).map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> search(String text) {
        return itemRepositoryInMemory.search(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public void throwIfUserNotFound(long userId) {
        if (userRepositoryInMemory.getById(userId) == null) {
            log.error("пользователя c идентификатором " + userId + " не существует");
            throw new NotFoundException("пользователя c идентификатором " + userId + " не существует");
        }
    }
}
