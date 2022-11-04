package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.List;

public interface UserService {

    List<UserDto> getAll();

    UserDto getById(long userId) throws UserNotFoundException;

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, long userId);

    void deleteById(long userId);
}
