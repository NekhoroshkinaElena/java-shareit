package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> add(@Valid @RequestBody ItemRequestDtoInput itemRequestDto,
                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.post(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getAllOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                          @Positive @RequestParam(required = false, defaultValue = "20") int size,
                                          @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getAll(from, size, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable("id") long requestId,
                                           @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getById(requestId, userId);
    }
}
