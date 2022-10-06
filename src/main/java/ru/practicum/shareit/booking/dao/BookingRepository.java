package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(long id);

    Booking findByBookerIdAndItem_IdAndEndBefore(long userId, long itemId, LocalDateTime time);

    List<Booking> findAllByItem_OwnerIdOrderByStartDesc(long id);

    Booking findByItem_IdAndEndBeforeAndStatusNotOrderByEndDesc(long itemId, LocalDateTime end, BookingStatus status);

    Booking findByItem_IdAndStatusAndStartAfterOrderByStartAsc(long itemId, BookingStatus status, LocalDateTime start);
}
