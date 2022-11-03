package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOutput save(@RequestBody BookingDtoInput bookingDtoInput,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.save(bookingDtoInput, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOutput approve(@PathVariable long bookingId, @RequestParam Boolean approved,
                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOutput getById(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoOutput> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.findAll(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoOutput> getAllOwner(@RequestHeader("X-Sharer-User-Id") long owner,
                                              @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.findAllOwner(owner, state);
    }
}
