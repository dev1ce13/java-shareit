package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemByRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class RequestMapperTest {

    @Test
    public void mapToItemRequestTest() {
        var requestDto = ItemRequestDto.builder()
                .id(1)
                .description("desc")
                .build();

        ItemRequest result = ItemRequestMapper.mapToItemRequest(requestDto, new User(1, "name", "email@mail.ru"));

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("desc", result.getDescription());
        Assertions.assertEquals(1, result.getRequester().getId());
    }

    @Test
    public void mapToItemRequestDtoTest() {
        var request = new ItemRequest(
                1,
                "desc",
                new User(1, "name", "email@mail.ru"),
                LocalDateTime.of(2020, 1, 1, 1, 1)
        );

        ItemRequestDto result = ItemRequestMapper.mapToItemRequestDto(request);

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("desc", result.getDescription());
        Assertions.assertEquals(1, result.getRequester().getId());
        Assertions.assertEquals(LocalDateTime.of(2020, 1, 1, 1, 1), request.getCreated());
    }

    @Test
    public void mapToItemRequestWithAnswerDtoTest() {
        var request = new ItemRequest(
                1,
                "desc",
                new User(1, "name", "email@mail.ru"),
                LocalDateTime.of(2020, 1, 1, 1, 1)
        );

        List<ItemByRequestDto> items = List.of(
                ItemByRequestDto.builder().id(1).build(),
                ItemByRequestDto.builder().id(2).build()
        );

        ItemRequestWithAnswerDto result = ItemRequestMapper.mapToItemRequestWithAnswerDto(request, items);

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("desc", result.getDescription());
        Assertions.assertEquals(1, result.getRequester().getId());
        Assertions.assertEquals(LocalDateTime.of(2020, 1, 1, 1, 1), request.getCreated());
        Assertions.assertEquals(1, result.getItems().get(0).getId());
        Assertions.assertEquals(2, result.getItems().get(1).getId());
    }
}
