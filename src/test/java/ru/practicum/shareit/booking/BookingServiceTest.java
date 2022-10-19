package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutput;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository repository;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;

    private Booking getBooking() {
        return new Booking(
                1,
                LocalDateTime.of(2020, 1, 1, 12, 12),
                LocalDateTime.of(2021, 3, 3, 3, 3),
                new Item(),
                new User(1, "name", "email@mail.ru"),
                BookingStatus.APPROVED
        );
    }

    private BookingDto getBookingDto() {
        return BookingDto.builder()
                .id(2)
                .start(LocalDateTime.of(2021, 2, 2, 11, 11))
                .end(LocalDateTime.now())
                .itemId(1)
                .bookerId(1)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    public void getByIdTest() {
        Mockito
                .when(repository.findById(anyLong()))
                .thenReturn(Optional.of(getBooking()));

        BookingOutput result = bookingService.getById(1, 1);

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals(LocalDateTime.of(2020, 1, 1, 12, 12), result.getStart());
        Assertions.assertEquals(LocalDateTime.of(2021, 3, 3, 3, 3), result.getEnd());
        Assertions.assertEquals(1, result.getBooker().getId());
        Assertions.assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    public void getByIdNotExistsTest() {
        Mockito
                .when(repository.findById(anyLong()))
                .thenThrow(new BookingNotFoundException("Booking with ID=1 not found"));

        final BookingNotFoundException exception = Assertions.assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.getById(1, 1));

        Assertions.assertEquals("Booking with ID=1 not found", exception.getMessage());
    }

    @Test
    public void getUserBookingsTest() {
        Mockito
                .when(repository.findAllByBookerIdOrderByStartDesc(anyLong()))
                .thenReturn(List.of(getBooking()));

        List<BookingOutput> result = bookingService.getUserBookings(BookingState.PAST, 1, 0, 1);

        Assertions.assertEquals(1, result.get(0).getId());
        Assertions.assertEquals(LocalDateTime.of(2020, 1, 1, 12, 12), result.get(0).getStart());
        Assertions.assertEquals(LocalDateTime.of(2021, 3, 3, 3, 3), result.get(0).getEnd());
        Assertions.assertEquals(1, result.get(0).getBooker().getId());
        Assertions.assertEquals(BookingStatus.APPROVED, result.get(0).getStatus());
    }

    @Test
    public void getBookingItemsByOwnerTest() {
        Mockito
                .when(repository.findAllByItem_OwnerIdOrderByStartDesc(anyLong()))
                .thenReturn(List.of(getBooking()));

        List<BookingOutput> result = bookingService.getBookingItemsByOwner(BookingState.PAST, 1, 0, 1);

        Assertions.assertEquals(1, result.get(0).getId());
        Assertions.assertEquals(LocalDateTime.of(2020, 1, 1, 12, 12), result.get(0).getStart());
        Assertions.assertEquals(LocalDateTime.of(2021, 3, 3, 3, 3), result.get(0).getEnd());
        Assertions.assertEquals(1, result.get(0).getBooker().getId());
        Assertions.assertEquals(BookingStatus.APPROVED, result.get(0).getStatus());
    }
}
