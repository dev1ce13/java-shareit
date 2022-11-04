package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutput;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class BookingMapperTest {

    @Test
    public void mapToBookingTest() {
        var bookingDto = BookingDto.builder()
                .id(1)
                .start(LocalDateTime.of(2021, 2, 2, 11, 11))
                .end(LocalDateTime.of(2021, 3, 3, 3, 3))
                .bookerId(1)
                .itemId(1)
                .status(BookingStatus.APPROVED)
                .build();

        Booking result = BookingMapper.mapToBooking(bookingDto);

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals(LocalDateTime.of(2021, 2, 2, 11, 11), result.getStart());
        Assertions.assertEquals(LocalDateTime.of(2021, 3, 3, 3, 3), result.getEnd());
        Assertions.assertNull(result.getBooker());
        Assertions.assertNull(result.getItem());
        Assertions.assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    public void mapToBookingDtoTest() {
        var booking = new Booking(
                1,
                LocalDateTime.of(2021, 2, 2, 11, 11),
                LocalDateTime.of(2021, 3, 3, 3, 3),
                new Item(),
                new User(),
                BookingStatus.APPROVED
        );

        BookingDto result = BookingMapper.mapToBookingDto(booking);

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals(LocalDateTime.of(2021, 2, 2, 11, 11), result.getStart());
        Assertions.assertEquals(LocalDateTime.of(2021, 3, 3, 3, 3), result.getEnd());
        Assertions.assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    public void mapToBookingOutputTest() {
        var booking = new Booking(
                1,
                LocalDateTime.of(2021, 2, 2, 11, 11),
                LocalDateTime.of(2021, 3, 3, 3, 3),
                new Item(),
                new User(1, "name", "email@mail.ru"),
                BookingStatus.APPROVED
        );

        BookingOutput result = BookingMapper.mapToBookingOutput(booking);

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals(LocalDateTime.of(2021, 2, 2, 11, 11), result.getStart());
        Assertions.assertEquals(LocalDateTime.of(2021, 3, 3, 3, 3), result.getEnd());
        Assertions.assertEquals(1, result.getBooker().getId());
        Assertions.assertEquals(BookingStatus.APPROVED, result.getStatus());
    }
}
