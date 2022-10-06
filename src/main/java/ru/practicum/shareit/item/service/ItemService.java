package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemsByOwnerDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto add(long userId, ItemDto itemDto);

    ItemDto update(long userId, ItemDto itemDto, long itemId);

    ItemsByOwnerDto getById(long itemId, long userId);

    List<ItemsByOwnerDto> getItemsByUserId(long userId);

    List<ItemDto> getSearch(String text);

    Item getItemById(long id);

    CommentDto addComment(CommentDto commentDto, long userId, long itemId);
}
