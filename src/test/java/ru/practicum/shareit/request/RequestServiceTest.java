package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    @Mock
    private ItemRequestRepository repository;
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;

    private ItemRequest getRequest() {
        return new ItemRequest(
                1,
                "desc",
                new User(1, "name", "email@mail.ru"),
                LocalDateTime.of(2020, 1, 1, 1, 1)
        );
    }

    private ItemRequestDto getRequestDto() {
        return ItemRequestDto.builder()
                .id(2)
                .description("desc2")
                .build();
    }

    @Test
    public void getUserRequestsTest() {
        Mockito
                .when(repository.findAllByRequester_IdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(getRequest()));

        List<ItemRequestWithAnswerDto> result = requestService.getUserRequests(1);

        Assertions.assertEquals(1, result.get(0).getId());
        Assertions.assertEquals("desc", result.get(0).getDescription());
        Assertions.assertEquals(1, result.get(0).getRequester().getId());
        Assertions.assertEquals(LocalDateTime.of(2020, 1, 1, 1, 1), result.get(0).getCreated());
    }

    @Test
    public void getRequestsTest() {
        Mockito
                .when(repository.findAllByRequester_IdNotOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(getRequest()));

        List<ItemRequestWithAnswerDto> result = requestService.getRequests(1, 0, 1);

        Assertions.assertEquals(1, result.get(0).getId());
        Assertions.assertEquals("desc", result.get(0).getDescription());
        Assertions.assertEquals(1, result.get(0).getRequester().getId());
        Assertions.assertEquals(LocalDateTime.of(2020, 1, 1, 1, 1), result.get(0).getCreated());
    }

    @Test
    public void getRequestsWithSizeIsNullTest() {
        Mockito
                .when(repository.findAllByRequester_IdNotOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(getRequest()));

        List<ItemRequestWithAnswerDto> result = requestService.getRequests(1, 0, null);

        Assertions.assertEquals(1, result.get(0).getId());
        Assertions.assertEquals("desc", result.get(0).getDescription());
        Assertions.assertEquals(1, result.get(0).getRequester().getId());
        Assertions.assertEquals(LocalDateTime.of(2020, 1, 1, 1, 1), result.get(0).getCreated());
    }

    @Test
    public void getRequestByIdTest() {
        Mockito
                .when(repository.findById(anyLong()))
                .thenReturn(Optional.of(getRequest()));

        ItemRequestWithAnswerDto result = requestService.getRequestById(1, 1);

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("desc", result.getDescription());
        Assertions.assertEquals(1, result.getRequester().getId());
        Assertions.assertEquals(LocalDateTime.of(2020, 1, 1, 1, 1), result.getCreated());
    }

    @Test
    public void getRequestNotExistsByIdTest() {
        Mockito
                .when(repository.findById(anyLong()))
                .thenThrow(new ItemRequestNotFoundException("Request with ID=1 not found"));

        final ItemRequestNotFoundException exception = Assertions.assertThrows(
                ItemRequestNotFoundException.class,
                () -> requestService.getRequestById(1, 1));

        Assertions.assertEquals("Request with ID=1 not found", exception.getMessage());
    }

    @Test
    public void getRequestsWithFromMoreListSizeTest() {
        Mockito
                .when(repository.findAllByRequester_IdNotOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(getRequest()));

        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> requestService.getRequests(1, 2, 1));

        Assertions.assertEquals("Parameter from must be lower size list", exception.getMessage());
    }


}
