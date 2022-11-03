package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentDtoOutput;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto item, long userId);

    //    ItemOutputDto getById(long id);
    ItemOutputDto getById(long id, long ownerId);

    List<ItemOutputDto> getAll(long userId);

    ItemDto update(ItemDto itemDto, long id, long userId);

    List<ItemDto> search(String text);

    CommentDtoOutput addComment(long itemId, long userId, CommentDto text);
}
