package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.exception.DuplicateEmailException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserDaoImpl implements UserDao {

    private final List<User> users = new ArrayList<>();
    private long id = 0;

    @Override
    public List<User> getAll() {
        return users;
    }

    @Override
    public User getById(long userId) {
         return users.stream()
                .filter(u -> u.getId() == userId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public User create(User user) {
        users.forEach(u -> {
            if (u.getEmail().equals(user.getEmail())) {
                throw new DuplicateEmailException(String.format("User with email=%s is exist", user.getEmail()));
            }
        });
        user.setId(getId());
        users.add(user);
        return user;
    }

    @Override
    public User update(User user) {
        users.forEach(u -> {
            if (u.getEmail().equals(user.getEmail())) {
                throw new DuplicateEmailException(String.format("User with email=%s is exist", user.getEmail()));
            }
        });
        for (User value : users) {
            if (value.getId() == user.getId()) {
                if (user.getName() != null) {
                    value.setName(user.getName());
                }
                if (user.getEmail() != null) {
                    value.setEmail(user.getEmail());
                }
            }
        }
        return users.stream()
                .filter(u -> u.getId() == user.getId())
                .findFirst()
                .orElse(null);
    }

    @Override
    public void deleteById(long userId) {
        for (User user : users) {
            if (user.getId() == userId) {
                users.remove(user);
                break;
            }
        }
    }

    private long getId() {
        return ++id;
    }
}
