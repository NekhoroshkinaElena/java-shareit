package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId, Pageable pageable);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId, Pageable pageable);

    Booking findFirstByItemIdAndEndBefore(long itemId, LocalDateTime date);

    Booking findFirstByItemIdAndStartAfter(long itemId, LocalDateTime date);

    Booking findBookingByItemIdAndBookerIdAndEndBefore(long itemId, long bookerId, LocalDateTime dateTime);
}
