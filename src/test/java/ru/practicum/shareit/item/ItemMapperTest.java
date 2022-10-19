package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemByRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemsByOwnerDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

public class ItemMapperTest {

    @Test
    public void mapToItemTest() {
        var itemDto = ItemDto.builder().id(1).name("name").description("desc").available(true).build();

        Item result = ItemMapper.mapToItem(itemDto, 1);

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("name", result.getName());
        Assertions.assertEquals("desc", result.getDescription());
        Assertions.assertTrue(result.getAvailable());
        Assertions.assertEquals(1, result.getOwnerId());
    }

    @Test
    public void mapToItemDtoTest() {
        var item = new Item(1, "name", "desc", true, 1, null);

        ItemDto result = ItemMapper.mapToItemDto(item);

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("name", result.getName());
        Assertions.assertEquals("desc", result.getDescription());
        Assertions.assertTrue(result.getAvailable());
    }

    @Test
    public void mapToItemsByOwnerDtoTest() {
        var item = new Item(1, "name", "desc", true, 1, null);

        ItemsByOwnerDto result = ItemMapper.mapToItemsByOwnerDto(item);

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("name", result.getName());
        Assertions.assertEquals("desc", result.getDescription());
        Assertions.assertTrue(result.getAvailable());
        Assertions.assertEquals(1, result.getOwnerId());
    }

    @Test
    public void mapToItemByRequestDtoTest() {
        var item = new Item(1, "name", "desc", true, 1, 1L);

        ItemByRequestDto result = ItemMapper.mapToItemByRequestDto(item);

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("name", result.getName());
        Assertions.assertEquals("desc", result.getDescription());
        Assertions.assertTrue(result.getAvailable());
        Assertions.assertEquals(1, result.getOwnerId());
        Assertions.assertEquals(1L, result.getRequestId());
    }
}
