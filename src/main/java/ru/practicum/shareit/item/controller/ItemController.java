package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDtoInput;
import ru.practicum.shareit.item.comment.CommentDtoOutput;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable long id,
                          @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.update(itemDto, id, userId);
    }

    @GetMapping("/{id}")
    public ItemOutputDto getById(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.getById(id, ownerId);
    }

    @GetMapping
    public List<ItemOutputDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(required = false) String text) {
        return itemService.search(text);
    }

    @PostMapping("/{id}/comment")
    public CommentDtoOutput addComment(@PathVariable(name = "id") long itemId,
                                       @RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestBody CommentDtoInput text) {
        return itemService.addComment(itemId, userId, text);
    }
}
