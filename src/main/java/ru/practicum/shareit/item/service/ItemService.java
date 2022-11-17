package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.CommentDtoInput;
import ru.practicum.shareit.item.comment.CommentDtoOutput;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto item, long userId);

    ItemOutputDto getById(long id, long ownerId);

    List<ItemOutputDto> getAll(long userId, int from, int size);

    ItemDto update(ItemDto itemDto, long itemId, long userId);

    List<ItemDto> search(String text, int from, int size);

    CommentDtoOutput addComment(long itemId, long userId, CommentDtoInput text);
}
