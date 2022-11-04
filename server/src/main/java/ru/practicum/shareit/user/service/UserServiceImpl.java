package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exception.IllegalUserException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.DuplicateEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    @Transactional
    public List<UserDto> getAll() {
        return repository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto getById(long userId) {
        Optional<User> user = repository.findById(userId);
        if (user.isPresent())
            return UserMapper.mapToUserDto(user.get());
        else
            throw new UserNotFoundException(String.format("User with ID=%s not found", userId));
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        try {
            User user = UserMapper.mapToUser(userDto);
            return UserMapper.mapToUserDto(repository.save(user));
        } catch (Exception e) {
            throw new DuplicateEmailException(String.format("User with email=%s already exists", userDto.getEmail()));
        }
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, long userId) {
        checkDuplicateEmail(userDto.getEmail());
        Optional<User> oldUser = repository.findById(userId);
        if (oldUser.isPresent()) {
            if (userId == oldUser.get().getId()) {
                User user = UserMapper.mapToUser(userDto);
                user.setId(userId);
                User newUser = buildingUser(user, oldUser.get());
                return UserMapper.mapToUserDto(repository.save(newUser));
            } else {
                throw new IllegalUserException(
                        String.format("User with ID=%s does not have access to user with ID=%s",
                                userId, oldUser.get().getId())
                );
            }
        } else {
            throw new UserNotFoundException(String.format("User with ID=%s not found", userId));
        }
    }

    @Override
    @Transactional
    public void deleteById(long userId) {
        getById(userId);
        repository.deleteById(userId);
    }

    private User buildingUser(User newUser, User oldUser) {
        if (newUser.getName() == null) {
            newUser.setName(oldUser.getName());
        }
        if (newUser.getEmail() == null) {
            newUser.setEmail(oldUser.getEmail());
        }
        return newUser;
    }

    private void checkDuplicateEmail(String email) {
        if (repository.existsByEmail(email) && !email.isBlank()) {
            throw new DuplicateEmailException(String.format("User with email=%s already exists", email));
        }
    }
}
