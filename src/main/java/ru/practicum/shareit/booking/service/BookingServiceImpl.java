package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutput;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingValidateException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    @Transactional
    public BookingOutput add(BookingDto bookingDto, long userId) {
        Booking booking = BookingMapper.mapToBooking(bookingDto);
        booking.setBooker(UserMapper.mapToUser(userService.getById(userId)));
        booking.setItem(itemService.getItemById(bookingDto.getItemId()));
        booking.setStatus(BookingStatus.WAITING);
        if (itemService.getItemById(booking.getItem().getId()).getOwnerId() != userId) {
            if (itemService.getItemById(booking.getItem().getId()).getAvailable())
                return BookingMapper.mapToBookingOutput(repository.save(booking));
            else
                throw new BookingValidateException(String.format("Item with ID=%s is not available",
                        booking.getItem().getId()));
        } else {
            throw new BookingNotFoundException(String.format("User with ID=%s can't book item with ID=%s",
                    userId, booking.getItem().getId()));
        }
    }

    @Override
    @Transactional
    public BookingOutput update(long bookingId, boolean approved, long userId) {
        userService.getById(userId);
        Optional<Booking> booking = repository.findById(bookingId);
        if (booking.isPresent()) {
            if (itemService.getItemById(booking.get().getItem().getId()).getOwnerId() == userId) {
                if (approved) {
                    if (booking.get().getStatus().equals(BookingStatus.APPROVED)) {
                        throw new IllegalArgumentException("This booking already have status APPROVED");
                    }
                    booking.get().setStatus(BookingStatus.APPROVED);
                } else {
                    booking.get().setStatus(BookingStatus.REJECTED);
                }
                return BookingMapper.mapToBookingOutput(repository.save(booking.get()));
            } else {
                throw new UserNotFoundException(String.format("User with ID=%s does not have access", userId));
            }
        } else {
            throw new BookingNotFoundException(String.format("Booking with ID=%s not found", bookingId));
        }
    }

    @Override
    @Transactional
    public BookingOutput getById(long bookingId, long userId) {
        userService.getById(userId);
        Optional<Booking> booking = repository.findById(bookingId);
        if (booking.isPresent()) {
            if (booking.get().getBooker().getId() == userId ||
                    itemService.getItemById(booking.get().getItem().getId()).getOwnerId() == userId) {
                return BookingMapper.mapToBookingOutput(booking.get());
            } else {
                throw new UserNotFoundException(String.format("User with ID=%s does not have access", userId));
            }
        } else {
            throw new BookingNotFoundException(String.format("Booking with ID=%s not found", bookingId));
        }
    }

    @Override
    @Transactional
    public List<BookingOutput> getUserBookings(BookingState state, long userId) {
        userService.getById(userId);
        List<Booking> bookings = repository.findAllByBookerIdOrderByStartDesc(userId);
        return filteringByState(bookings, state);
    }

    @Override
    @Transactional
    public List<BookingOutput> getBookingItemsByOwner(BookingState state, long userId) {
        userService.getById(userId);
        List<Booking> bookings = repository.findAllByItem_OwnerIdOrderByStartDesc(userId);
        return filteringByState(bookings, state);
    }

    @Transactional
    private List<BookingOutput> filteringByState(List<Booking> bookings, BookingState state) {
        if (state.equals(BookingState.CURRENT)) {
            return bookings.stream()
                    .filter(booking -> booking.getStart().isBefore(LocalDateTime.now())
                            && booking.getEnd().isAfter(LocalDateTime.now()))
                    .map(BookingMapper::mapToBookingOutput)
                    .collect(Collectors.toList());
        } else if (state.equals(BookingState.FUTURE)) {
            return bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now())
                            && !booking.getStatus().equals(BookingStatus.REJECTED))
                    .map(BookingMapper::mapToBookingOutput)
                    .collect(Collectors.toList());
        } else if (state.equals(BookingState.PAST)) {
            return bookings.stream()
                    .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                    .map(BookingMapper::mapToBookingOutput)
                    .collect(Collectors.toList());
        } else if (state.equals(BookingState.WAITING)) {
            return bookings.stream()
                    .filter(booking -> booking.getStatus().equals(BookingStatus.WAITING))
                    .map(BookingMapper::mapToBookingOutput)
                    .collect(Collectors.toList());
        } else if (state.equals(BookingState.REJECTED)) {
            return bookings.stream()
                    .filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED))
                    .map(BookingMapper::mapToBookingOutput)
                    .collect(Collectors.toList());
        } else {
            return bookings.stream()
                    .map(BookingMapper::mapToBookingOutput)
                    .collect(Collectors.toList());
        }
    }
}
