package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {

    List<User> getAll();

    User getById(long userId);

    User create(User user);

    User update(User user);

    void deleteById(long userId);
}
