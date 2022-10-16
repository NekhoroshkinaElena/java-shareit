package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
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
    public ItemDto createItem(@Valid @RequestBody Item item, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.createItem(item, userId);
    }

    @PatchMapping("/{id}")
    public Item updateItem(@RequestBody Item item, @PathVariable long id,
                           @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.itemUpdate(item, id, userId);
    }

    @GetMapping
    public List<Item> getItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable long id) {
        return itemService.getItemById(id);
    }

    @GetMapping("/search")
    public List<Item> searchItem(@RequestParam(required = false) String text) {
        return itemService.searchItems(text);
    }
}
