package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDtoOutput save(BookingDtoInput bookingDtoInput, long userId) {
        Item item = getItem(bookingDtoInput.getItemId());
        if (item.getOwner().getId() == userId) {
            log.error("Владелец вещи не может её забронировать.");
            throw new NotFoundException("Владелец вещи не может её забронировать.");
        }
        if (bookingDtoInput.getStart().isAfter(bookingDtoInput.getEnd()) ||
                bookingDtoInput.getEnd().isBefore(bookingDtoInput.getStart()) ||
                bookingDtoInput.getStart().isBefore(LocalDateTime.now())) {
            log.error("Установите корректное время для бронирования.");
            throw new ValidationException("Установите корректное время для бронирования.");
        }
        Booking booking = BookingMapper.toBooking(bookingDtoInput);
        booking.setItem(item);
        booking.setBooker(getUser(userId));
        if (!booking.getItem().getAvailable()) {
            log.error("Вещь занята другим пользователем.");
            throw new ValidationException("Вещь занята другим пользователем.");
        }
        return BookingMapper.toBookingDtoOutput(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoOutput approve(long userId, long bookingId, boolean status) {
        throwIfUserNotFound(userId);
        Booking booking = getBooking(bookingId);
        if (booking.getItem().getOwner().getId() != userId) {
            log.error("Подтверждение или отклонение запроса на бронирование может быть выполнено " +
                    "только владельцем вещи.");
            throw new NotFoundException("Подтверждение или отклонение запроса на бронирование может быть выполнено " +
                    "только владельцем вещи.");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            log.error("Статус бронирования уже подтверждён.");
            throw new ValidationException("Статус бронирования уже подтверждён.");
        }
        booking.setStatus(status ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingDtoOutput(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoOutput getById(long bookingId, long userId) {
        throwIfUserNotFound(userId);
        Booking booking = getBooking(bookingId);
        Item item = getItem(booking.getItem().getId());
        if (booking.getBooker().getId() != userId) {
            if (item.getOwner().getId() != userId) {
                log.error("Получить данные о бронировании может либо автор бронирования либо владелец вещи.");
                throw new NotFoundException("Получить данные о бронировании может либо автор бронирования" +
                        " либо владелец вещи.");
            }
        }
        return BookingMapper.toBookingDtoOutput(booking);
    }

    @Override
    public List<BookingDtoOutput> findAllForBooker(int from, int size, long bookerId, String state) {
        throwIfUserNotFound(bookerId);
        return findBookings(bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId,
                PageRequest.of(from / size, size)), state).stream()
                .map(BookingMapper::toBookingDtoOutput).collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoOutput> findAllForOwner(int from, int size, long ownerId, String state) {
        throwIfUserNotFound(ownerId);
        //throwIfWrongParam(from, size, ownerId);
        if (itemRepository.findAllByOwnerId(ownerId).isEmpty()) {
            log.error("Вы не можете получить список бронирований, так как у вас нет вещей.");
            throw new ValidationException("Вы не можете получить список бронирований, так как у вас нет вещей.");
        }
        return findBookings(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId,
                PageRequest.of(from / size, size)), state).stream()
                .map(BookingMapper::toBookingDtoOutput).collect(Collectors.toList());
    }

    public List<Booking> findBookings(List<Booking> bookings, String state) {
        switch (state) {
            case "WAITING":
                return bookings.stream().filter(booking -> booking.getStatus().equals(BookingStatus.WAITING))
                        .collect(Collectors.toList());
            case "PAST":
                return bookings.stream().filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookings.stream().filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookings.stream().filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED))
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookings.stream().filter(booking -> booking.getStart().isBefore(LocalDateTime.now())
                        && booking.getEnd().isAfter(LocalDateTime.now())).collect(Collectors.toList());
            default:
                return bookings;
        }
    }

    public void throwIfUserNotFound(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.error("Пользователя c идентификатором " + userId + " не существует.");
            throw new NotFoundException("пользователя c идентификатором " + userId + " не существует.");
        }
    }

    public Booking getBooking(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.error("Такого бронирования не существует.");
                    return new NotFoundException("Такого бронирования не существует.");
                });
    }

    private Item getItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Вещь с id " + itemId + " не найдена.");
                    return new NotFoundException("Вещь с id " + itemId + " не найдена.");
                });
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователя c идентификатором " + userId + " не существует.");
                    return new NotFoundException("Пользователя c идентификатором " + userId + " не существует.");
                });
    }
}
