package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemByRequestDto;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class RequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService requestService;

    @Autowired
    private MockMvc mvc;

    private final ItemRequestDto requestDto = ItemRequestDto.builder()
            .id(1L)
            .description("text")
            .created(LocalDateTime.of(2020, 1, 1, 1, 1, 1))
            .requester(new User(1L, "name", "email@mail.ru"))
            .build();

    private final ItemRequestWithAnswerDto requestWithAnswerDto = ItemRequestWithAnswerDto.builder()
            .id(2L)
            .description("text2")
            .created(LocalDateTime.of(2020, 1, 1, 1, 1, 1))
            .requester(new User(1L, "name", "email@mail.ru"))
            .items(List.of(ItemByRequestDto.builder().id(1L).build()))
            .build();

    @Test
    void addRequest() throws Exception {
        when(requestService.addRequest(any(), anyLong()))
                .thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(String.valueOf(requestDto.getDescription()))))
                .andExpect(jsonPath("$.requester", is(requestDto.getRequester()), User.class))
                .andExpect(jsonPath("$.created", is(String.valueOf(requestDto.getCreated()))));
    }

    @Test
    void getUserRequests() throws Exception {
        when(requestService.getUserRequests(anyLong()))
                .thenReturn(List.of(requestWithAnswerDto));

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestWithAnswerDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestWithAnswerDto.getDescription())))
                .andExpect(jsonPath("$[0].requester", is(requestWithAnswerDto.getRequester()), User.class))
                .andExpect(jsonPath("$[0].created", is(String.valueOf(requestWithAnswerDto.getCreated()))));
    }

    @Test
    void getRequests() throws Exception {
        when(requestService.getRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(requestWithAnswerDto));

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestWithAnswerDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestWithAnswerDto.getDescription())))
                .andExpect(jsonPath("$[0].requester", is(requestWithAnswerDto.getRequester()), User.class))
                .andExpect(jsonPath("$[0].created", is(String.valueOf(requestWithAnswerDto.getCreated()))));
    }

    @Test
    void getRequestById() throws Exception {
        when(requestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(requestWithAnswerDto);

        mvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestWithAnswerDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestWithAnswerDto.getDescription())))
                .andExpect(jsonPath("$.requester", is(requestWithAnswerDto.getRequester()), User.class))
                .andExpect(jsonPath("$.created", is(String.valueOf(requestWithAnswerDto.getCreated()))));
    }
}
