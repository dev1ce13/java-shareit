package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao dao;
    private final UserService userService;

    @Override
    public ItemDto add(long userId, ItemDto itemDto) {
        userService.getById(userId);
        Item item = ItemMapper.mapToItem(itemDto, userId);
        return ItemMapper.mapToItemDto(dao.add(item));
    }

    @Override
    public ItemDto update(long userId, ItemDto itemDto, long itemId) {
        getById(itemId);
        userService.getById(userId);
        Item item = ItemMapper.mapToItem(itemDto, userId);
        item.setId(itemId);
        return ItemMapper.mapToItemDto(dao.update(item, userId));
    }

    @Override
    public ItemDto getById(long itemId) {
        Item item = dao.getById(itemId);
        if (item == null) {
            throw new ItemNotFoundException(String.format("Item with ID=%s doesn't exist", itemId));
        }
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByUserId(long userId) {
        userService.getById(userId);
        return dao.getAll()
                .stream()
                .filter(item -> item.getOwnerId() == userId)
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getSearch(String text) {
        if (text.isEmpty()) {
            return List.of();
        }
        List<Item> items = dao.getAll();
        return items.stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }
}
