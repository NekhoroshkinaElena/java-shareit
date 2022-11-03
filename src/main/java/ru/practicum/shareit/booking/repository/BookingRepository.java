package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker_IdOrderByStartDesc(long bookerId);

    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(long bookerId, BookingStatus status);

    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime date);

    List<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime date);

    List<Booking> findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(long bookerId, LocalDateTime dateTime,
                                                                              LocalDateTime dateTime2);

    List<Booking> findAllByItem_OwnerIdAndStatusOrderByStartDesc(long ownerId, BookingStatus state);

    List<Booking> findAllByItem_OwnerIdOrderByStartDesc(long ownerId);

    List<Booking> findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(long ownerId, LocalDateTime date);

    List<Booking> findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime date);

    List<Booking> findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(long bookerId, LocalDateTime dateTime,
                                                                                 LocalDateTime dateTime2);

    Booking findByItem_IdAndEndBefore(long itemId, LocalDateTime date);

    Booking findFirstByItem_IdAndStartAfter(long itemId, LocalDateTime date);

    Booking findBookingByItem_IdAndBooker_IdAndEndBefore(long itemId, long bookerId, LocalDateTime dateTime);
}
