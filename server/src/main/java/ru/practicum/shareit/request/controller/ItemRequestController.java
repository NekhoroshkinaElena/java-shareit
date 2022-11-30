package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestServiceImpl itemRequestService;

    @PostMapping
    public ItemRequestDtoOutput add(@Valid @RequestBody ItemRequestDtoInput itemRequestDto,
                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.add(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDtoOutput> findAllOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.findAllOwner(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOutput> findAll(@RequestParam(required = false, defaultValue = "0") int from,
                                              @RequestParam(required = false, defaultValue = "20") int size,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.findAll(from, size, userId);
    }

    @GetMapping("/{id}")
    public ItemRequestDtoOutput findById(@PathVariable("id") long requestId,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.findById(requestId, userId);
    }
}
