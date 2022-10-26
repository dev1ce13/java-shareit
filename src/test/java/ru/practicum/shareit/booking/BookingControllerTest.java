package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutput;
import ru.practicum.shareit.booking.exception.BookingValidateException;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final BookingDto bookingDto = BookingDto.builder()
            .id(1L)
            .start(LocalDateTime.of(2023, 1, 1, 1, 1))
            .end(LocalDateTime.of(2024, 1, 1, 1, 1))
            .bookerId(1L)
            .itemId(1L)
            .build();

    private final BookingOutput bookingOutput = BookingOutput.builder()
            .id(2L)
            .start(LocalDateTime.of(2023, 1, 1, 1, 1))
            .end(LocalDateTime.of(2024, 1, 1, 1, 1))
            .item(new Item(1L, "name", "desc", true, 1L, null))
            .booker(new User(1L, "name", "email@mail.ru"))
            .status(BookingStatus.WAITING)
            .build();

    @Test
    void addBooking() throws Exception {
        when(bookingService.add(any(), anyLong()))
                .thenReturn(bookingOutput);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutput.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(String.valueOf(bookingOutput.getStatus()))))
                .andExpect(jsonPath("$.booker", is(bookingOutput.getBooker()), User.class))
                .andExpect(jsonPath("$.item", is(bookingOutput.getItem()), Item.class));
    }

    @Test
    void updateBooking() throws Exception {
        when(bookingService.update(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(bookingOutput);

        mvc.perform(patch("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutput.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(String.valueOf(bookingOutput.getStatus()))))
                .andExpect(jsonPath("$.booker", is(bookingOutput.getBooker()), User.class))
                .andExpect(jsonPath("$.item", is(bookingOutput.getItem()), Item.class));
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(bookingOutput);

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutput.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(String.valueOf(bookingOutput.getStatus()))))
                .andExpect(jsonPath("$.booker", is(bookingOutput.getBooker()), User.class))
                .andExpect(jsonPath("$.item", is(bookingOutput.getItem()), Item.class));
    }

    @Test
    void getUserBookings() throws Exception {
        when(bookingService.getUserBookings(any(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingOutput));

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingOutput.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(String.valueOf(bookingOutput.getStatus()))))
                .andExpect(jsonPath("$[0].booker", is(bookingOutput.getBooker()), User.class))
                .andExpect(jsonPath("$[0].item", is(bookingOutput.getItem()), Item.class));
    }

    @Test
    void getBookingItems() throws Exception {
        when(bookingService.getBookingItemsByOwner(any(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingOutput));

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingOutput.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(String.valueOf(bookingOutput.getStatus()))))
                .andExpect(jsonPath("$[0].booker", is(bookingOutput.getBooker()), User.class))
                .andExpect(jsonPath("$[0].item", is(bookingOutput.getItem()), Item.class));
    }

    @Test
    void addNotValidBooking() throws Exception {
        when(bookingService.add(any(), anyLong()))
                .thenThrow(new BookingValidateException("Data does not validation"));

        final BookingValidateException exception = Assertions.assertThrows(
                BookingValidateException.class,
                () -> bookingService.add(bookingDto, 1L));

        Assertions.assertEquals("Data does not validation", exception.getMessage());

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is(exception.getMessage())));
    }
}
