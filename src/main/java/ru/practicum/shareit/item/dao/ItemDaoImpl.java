package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.exception.IllegalUserException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemDaoImpl implements ItemDao {

    private final List<Item> items = new ArrayList<>();
    private long id = 0;

    @Override
    public Item add(Item item) {
        item.setId(getId());
        items.add(item);
        return item;
    }

    @Override
    public Item update(Item item, long userId) {
        Item oldItem = getById(item.getId());
        if (oldItem.getOwnerId() == userId) {
            for (Item value : items) {
                if (value.getId() == item.getId()) {
                    if (item.getName() != null) {
                        value.setName(item.getName());
                    }
                    if (item.getDescription() != null) {
                        value.setDescription(item.getDescription());
                    }
                    if (item.getAvailable() != null) {
                        value.setAvailable(item.getAvailable());
                    }
                }
            }
            return items.stream()
                    .filter(i -> i.getId() == item.getId())
                    .findFirst()
                    .orElse(null);
        } else {
            throw new IllegalUserException(String.format("user with ID=%s does not have access to this item", userId));
        }
    }

    @Override
    public Item getById(long itemId) {
        return items.stream()
                .filter(i -> i.getId() == itemId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Item> getAll() {
        return items;
    }

    private long getId() {
        return ++id;
    }
}
