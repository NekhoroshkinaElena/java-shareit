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
        if (bookingDtoInput.getStart().isAfter(bookingDtoInput.getEnd()) ||
                bookingDtoInput.getEnd().isBefore(bookingDtoInput.getStart()) ||
                bookingDtoInput.getStart().isBefore(LocalDateTime.now())) {
            log.error("установите корректное время для бронирования");
            throw new ValidationException("установите корректное время для бронирования");
        }

        if (itemRepository.findById(bookingDtoInput.getItemId()).get().getOwner().getId() == userId) {
            log.error("владелец вещи не может её забронировать");
            throw new NotFoundException("владелец вещи не может её забронировать");
        }

        Booking booking = new Booking();
        booking.setItem(itemRepository.findById(bookingDtoInput.getItemId()).get());
        booking.setStart(bookingDtoInput.getStart());
        booking.setEnd(bookingDtoInput.getEnd());
        booking.setStatus(BookingStatus.WAITING);
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
        if (status) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDtoOutput(bookingRepository.save(booking));
    }

    public BookingDtoOutput getById(long bookingId, long userId) {
        if (bookingRepository.findById(bookingId).isEmpty()) {
            log.error("такой вещи не существует");
            throw new NotFoundException("такой вещи не существует");
        }

        if (bookingRepository.findById(bookingId).get().getBooker().getId() != userId) {
            if (itemRepository.findById(
                    bookingRepository.findById(bookingId).get().getItem().getId()).get().getOwner().getId() != userId) {
                log.error("неверный пользователь");
                throw new NotFoundException("неверный пользователь");
            }
        }
        return BookingMapper.toBookingDtoOutput(bookingRepository.findById(bookingId).get());
    }

    @Override
    public List<BookingDtoOutput> findAll(long bookerId, String state) {
        throwIfUserNotFound(bookerId);
        if (itemRepository.findById(bookerId).isEmpty()) {
            log.error("такой вещи не существует");
            throw new NotFoundException("такой вещи не существует");
        }
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByBooker_IdOrderByStartDesc(bookerId).
                        stream().map(BookingMapper::toBookingDtoOutput).collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING).
                        stream().map(BookingMapper::toBookingDtoOutput).collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now()).
                        stream().map(BookingMapper::toBookingDtoOutput).collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now()).
                        stream().map(BookingMapper::toBookingDtoOutput).collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED).
                        stream().map(BookingMapper::toBookingDtoOutput).collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc
                                (bookerId, LocalDateTime.now(), LocalDateTime.now()).
                        stream().map(BookingMapper::toBookingDtoOutput).collect(Collectors.toList());
            default:
                log.error("неверный статус");
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingDtoOutput> findAllOwner(long ownerId, String state) {
        throwIfUserNotFound(ownerId);
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(ownerId).
                        stream().map(BookingMapper::toBookingDtoOutput).collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING).
                        stream().map(BookingMapper::toBookingDtoOutput).collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now()).
                        stream().map(BookingMapper::toBookingDtoOutput).collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now()).
                        stream().map(BookingMapper::toBookingDtoOutput).collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED).
                        stream().map(BookingMapper::toBookingDtoOutput).collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc
                                (ownerId, LocalDateTime.now(), LocalDateTime.now()).
                        stream().map(BookingMapper::toBookingDtoOutput).collect(Collectors.toList());
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
}
