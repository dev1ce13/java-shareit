package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao dao;

    @Override
    public List<UserDto> getAll() {
        return dao.getAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(long userId) {
        User user = dao.getById(userId);
        if (user == null) {
            throw new UserNotFoundException(String.format("User with ID=%s doesn't exist", userId));
        }
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = dao.create(UserMapper.mapToUser(userDto));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto update(UserDto userDto, long userId) {
        getById(userId);
        User user = UserMapper.mapToUser(userDto);
        user.setId(userId);
        return UserMapper.mapToUserDto(dao.update(user));
    }

    @Override
    public void deleteById(long userId) {
        getById(userId);
        dao.deleteById(userId);
    }
}
