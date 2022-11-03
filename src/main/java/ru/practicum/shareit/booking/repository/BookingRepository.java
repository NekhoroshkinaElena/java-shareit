package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker_IdOrderByStartDesc(long bookerId);

    List<Booking> findAllByItem_OwnerIdOrderByStartDesc(long ownerId);

    Booking findByItem_IdAndEndBefore(long itemId, LocalDateTime date);

    Booking findFirstByItem_IdAndStartAfter(long itemId, LocalDateTime date);

    Booking findBookingByItem_IdAndBooker_IdAndEndBefore(long itemId, long bookerId, LocalDateTime dateTime);
}
