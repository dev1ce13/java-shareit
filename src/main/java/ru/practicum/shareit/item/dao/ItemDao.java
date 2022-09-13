package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {

    Item add(Item item);

    Item update(Item item, long userId);

    Item getById(long itemId);

    List<Item> getAll();
}
