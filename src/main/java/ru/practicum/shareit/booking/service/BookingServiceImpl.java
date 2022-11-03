package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
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
        if (itemRepository.findById(bookingDtoInput.getItemId()).isEmpty()) {
            log.error("такой вещи не существует");
            throw new NotFoundException("такой вещи не существует");
        }
        if (itemRepository.findById(bookingDtoInput.getItemId()).get().getOwner().getId() == userId) {
            log.error("владелец вещи не может её забронировать");
            throw new NotFoundException("владелец вещи не может её забронировать");
        }
        if (bookingDtoInput.getStart().isAfter(bookingDtoInput.getEnd()) ||
                bookingDtoInput.getEnd().isBefore(bookingDtoInput.getStart()) ||
                bookingDtoInput.getStart().isBefore(LocalDateTime.now())) {
            log.error("установите корректное время для бронирования");
            throw new ValidationException("установите корректное время для бронирования");
        }
        Booking booking = BookingMapper.toBooking(bookingDtoInput);
        booking.setItem(itemRepository.findById(bookingDtoInput.getItemId()).get());
        booking.setBooker(userRepository.findById(userId).get());
        if (!booking.getItem().getAvailable()) {
            log.error("вещь занята другим пользователем");
            throw new ValidationException("вещь занята другим пользователем");
        }
        return BookingMapper.toBookingDtoOutput(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoOutput approve(long userId, long bookingId, boolean status) {
        throwIfUserNotFound(userId);
        if (bookingRepository.findById(bookingId).get().getItem().getOwner().getId() != userId) {
            log.error("неверный пользователь");
            throw new NotFoundException("неверный пользователь");
        }
        Booking booking = bookingRepository.findById(bookingId).get();
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            log.error("статус уже подтверждён");
            throw new ValidationException("статус уже подтверждён");
        }
        booking.setStatus(status ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingDtoOutput(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoOutput getById(long bookingId, long userId) {
        throwIfItemNotFound(bookingId);
        if (bookingRepository.findById(bookingId).get().getBooker().getId() != userId) {
            if (itemRepository.findById(bookingRepository.findById(bookingId)
                    .get().getItem().getId()).get().getOwner().getId() != userId) {
                log.error("получить данные о бронировании может либо автор бронирования либо владелец вещи");
                throw new NotFoundException("получить данные о бронировании может либо автор бронирования" +
                        " либо владелец вещи");
            }
        }
        return BookingMapper.toBookingDtoOutput(bookingRepository.findById(bookingId).get());
    }

    @Override
    public List<BookingDtoOutput> findAll(long bookerId, String state) {
        throwIfUserNotFound(bookerId);
        return findBooking(bookingRepository.findAllByBooker_IdOrderByStartDesc(bookerId), state).stream()
                .map(BookingMapper::toBookingDtoOutput).collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoOutput> findAllOwner(long ownerId, String state) {
        throwIfUserNotFound(ownerId);
        return findBooking(bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(ownerId), state).stream()
                .map(BookingMapper::toBookingDtoOutput).collect(Collectors.toList());
    }

    public List<Booking> findBooking(List<Booking> bookings, String state) {
        switch (state) {
            case "ALL":
                return bookings;
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
                log.error("неверный статус");
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    public void throwIfUserNotFound(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.error("пользователя c идентификатором " + userId + " не существует");
            throw new NotFoundException("пользователя c идентификатором " + userId + " не существует");
        }
    }

    public void throwIfItemNotFound(long bookingId) {
        if (bookingRepository.findById(bookingId).isEmpty()) {
            log.error("такой вещи не существует");
            throw new NotFoundException("такой вещи не существует");
        }
    }
}
