package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemsByOwnerDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    public void updateTest() {
        Mockito
                .when(repository.findById(anyLong()))
                .thenReturn(Optional.of(getItem()));
        Mockito
                .when(repository.save(any()))
                .thenReturn(getItem());

        var result = itemService.update(1L, getItemDto(), 1L);

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
    public void getSearchWithSizeNotIsNullTest() {
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

    @Test
    public void getSearchWithSizeIsNullTest() {
        Mockito
                .when(repository.findAllByNameOrDescriptionContainingIgnoreCase(anyString(), anyString()))
                .thenReturn(List.of(getItem()));

        List<ItemDto> result = itemService.getSearch("name", 0, null);

        Assertions.assertEquals(1, result.get(0).getId());
        Assertions.assertEquals("name", result.get(0).getName());
        Assertions.assertEquals("desc", result.get(0).getDescription());
        Assertions.assertEquals(true, result.get(0).getAvailable());
        Assertions.assertNull(result.get(0).getRequestId());
    }

    @Test
    public void addCommentTest() {
        Mockito
                .when(userService.getById(anyLong()))
                .thenReturn(UserDto.builder().build());
        Mockito
                .when(bookingRepository.findByBookerIdAndItem_IdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(new Booking());
        Mockito
                .when(commentRepository.save(any()))
                .thenReturn(new Comment(1, "text", new User(), 1, LocalDateTime.now()));

        var result = itemService.addComment(CommentDto.builder().build(), 1L, 1L);

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("text", result.getText());
        Assertions.assertEquals(1, result.getItemId());
    }

    @Test
    public void getItemsByUserIdWithSizeNotIsNullTest() {
        Mockito
                .when(repository.findAllByOwnerId(anyLong()))
                .thenReturn(List.of(getItem()));
        Mockito
                .when(bookingRepository.findByItem_IdAndEndBeforeAndStatusNotOrderByEndDesc(anyLong(), any(), any()))
                .thenReturn(new Booking(1, LocalDateTime.now(), LocalDateTime.now(), new Item(), new User(), BookingStatus.APPROVED));
        Mockito
                .when(bookingRepository.findByItem_IdAndEndBeforeAndStatusNotOrderByEndDesc(anyLong(), any(), any()))
                .thenReturn(new Booking(1, LocalDateTime.now(), LocalDateTime.now(), new Item(), new User(), BookingStatus.APPROVED));
        Mockito.when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(Set.of(new Comment(1, "text", new User(), 1, LocalDateTime.now())));

        List<ItemsByOwnerDto> result = itemService.getItemsByUserId(1L, 0, 1);

        Assertions.assertEquals(1, result.get(0).getId());
        Assertions.assertEquals("name", result.get(0).getName());
        Assertions.assertEquals("desc", result.get(0).getDescription());
        Assertions.assertEquals(true, result.get(0).getAvailable());
        Assertions.assertNull(result.get(0).getRequestId());
    }

    @Test
    public void getItemsByUserIdWithSizeIsNullTest() {
        Mockito
                .when(repository.findAllByOwnerId(anyLong()))
                .thenReturn(List.of(getItem()));
        Mockito
                .when(bookingRepository.findByItem_IdAndEndBeforeAndStatusNotOrderByEndDesc(anyLong(), any(), any()))
                .thenReturn(new Booking(1, LocalDateTime.now(), LocalDateTime.now(), new Item(), new User(), BookingStatus.APPROVED));
        Mockito
                .when(bookingRepository.findByItem_IdAndEndBeforeAndStatusNotOrderByEndDesc(anyLong(), any(), any()))
                .thenReturn(new Booking(1, LocalDateTime.now(), LocalDateTime.now(), new Item(), new User(), BookingStatus.APPROVED));
        Mockito.when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(Set.of(new Comment(1, "text", new User(), 1, LocalDateTime.now())));

        List<ItemsByOwnerDto> result = itemService.getItemsByUserId(1L, 0, null);

        Assertions.assertEquals(1, result.get(0).getId());
        Assertions.assertEquals("name", result.get(0).getName());
        Assertions.assertEquals("desc", result.get(0).getDescription());
        Assertions.assertEquals(true, result.get(0).getAvailable());
        Assertions.assertNull(result.get(0).getRequestId());
    }
}
