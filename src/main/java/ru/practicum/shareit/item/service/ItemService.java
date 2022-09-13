package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto add(long userId, ItemDto itemDto);

    ItemDto update(long userId, ItemDto itemDto, long itemId);

    ItemDto getById(long itemId);

    List<ItemDto> getItemsByUserId(long userId);

    List<ItemDto> getSearch(String text);
}
