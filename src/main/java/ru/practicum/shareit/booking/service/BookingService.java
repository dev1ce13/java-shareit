package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutput;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingOutput add(BookingDto bookingDto, long userId);

    BookingOutput update(long bookingId, boolean approved, long userId);

    BookingOutput getById(long bookingId, long userId);

    List<BookingOutput> getUserBookings(BookingState state, long userId);

    List<BookingOutput> getBookingItemsByOwner(BookingState state, long userId);
}
