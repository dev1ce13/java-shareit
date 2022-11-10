package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutput;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService service;

    @PostMapping
    public BookingOutput add(@RequestBody BookingDto bookingDto,
                             @RequestHeader("X-Sharer-User-Id") long userId) {
        return service.add(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutput update(@PathVariable long bookingId,
                                @RequestParam boolean approved,
                                @RequestHeader("X-Sharer-User-Id") long userId) {
        return service.update(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingOutput getBooking(@PathVariable long bookingId,
                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        return service.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingOutput> getUserBookings(
            @RequestParam(name = "state", defaultValue = "ALL", required = false) String value,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "from", defaultValue = "0", required = false) int from,
            @RequestParam(name = "size", required = false) Integer size
    ) {
        BookingState state = BookingState.from(value);
        return service.getUserBookings(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingOutput> getBookingItems(
            @RequestParam(defaultValue = "ALL", required = false, name = "state") String value,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "from", defaultValue = "0", required = false) int from,
            @RequestParam(name = "size", required = false) Integer size
    ) {
        BookingState state = BookingState.from(value);
        return service.getBookingItemsByOwner(state, userId, from, size);
    }
}
