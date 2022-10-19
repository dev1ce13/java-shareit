package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemsByOwnerDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository repository;
    @Mock
    private UserService userService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    private Item getItem() {
        return new Item(1, "name", "desc", true, 1, null);
    }

    private ItemDto getItemDto() {
        return ItemDto.builder()
                .name("name2")
                .description("desc2")
                .available(true)
                .build();
    }

    @Test
    public void addTest() {
        Mockito
                .when(repository.save(any(Item.class)))
                .thenReturn(getItem());

        ItemDto result = itemService.add(1, getItemDto());

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("name", result.getName());
        Assertions.assertEquals("desc", result.getDescription());
        Assertions.assertEquals(true, result.getAvailable());
        Assertions.assertNull(result.getRequestId());
    }

    @Test
    public void getByIdTest() {
        Mockito
                .when(repository.findById(anyLong()))
                .thenReturn(Optional.of(getItem()));

        ItemsByOwnerDto result = itemService.getById(1, 1);

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("name", result.getName());
        Assertions.assertEquals("desc", result.getDescription());
        Assertions.assertEquals(true, result.getAvailable());
        Assertions.assertEquals(1, result.getOwnerId());
        Assertions.assertNull(result.getRequestId());
    }

    @Test
    public void getItemByIdNotExistsTest() {
        Mockito
                .when(repository.findById(anyLong()))
                .thenThrow(new ItemNotFoundException("Item with ID=1 doesn't exist"));

        final ItemNotFoundException exception = Assertions.assertThrows(
                ItemNotFoundException.class,
                () -> itemService.getItemById(1));

        Assertions.assertEquals("Item with ID=1 doesn't exist", exception.getMessage());
    }

    @Test
    public void getItemsByUserIdTest() {
        Mockito
                .when(repository.findAllByOwnerId(anyLong()))
                .thenReturn(List.of(getItem()));

        List<ItemsByOwnerDto> result = itemService.getItemsByUserId(1, 0, 1);

        Assertions.assertEquals(1, result.get(0).getId());
        Assertions.assertEquals("name", result.get(0).getName());
        Assertions.assertEquals("desc", result.get(0).getDescription());
        Assertions.assertEquals(true, result.get(0).getAvailable());
        Assertions.assertEquals(1, result.get(0).getOwnerId());
        Assertions.assertNull(result.get(0).getRequestId());
    }

    @Test
    public void getSearchTest() {
        Mockito
                .when(repository.findAllByNameOrDescriptionContainingIgnoreCase(anyString(), anyString()))
                .thenReturn(List.of(getItem()));

        List<ItemDto> result = itemService.getSearch("name", 0, 1);

        Assertions.assertEquals(1, result.get(0).getId());
        Assertions.assertEquals("name", result.get(0).getName());
        Assertions.assertEquals("desc", result.get(0).getDescription());
        Assertions.assertEquals(true, result.get(0).getAvailable());
        Assertions.assertNull(result.get(0).getRequestId());
    }
}
