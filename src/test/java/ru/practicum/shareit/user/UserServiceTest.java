package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.exception.IllegalUserException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.DuplicateEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository repository;

    private User getUser() {
        return new User(1, "name", "asd@mail.ru");
    }

    private UserDto getUserDto() {
        return UserDto.builder()
                .name("name2")
                .email("email2@mail.ru")
                .build();
    }

    @Test
    public void getAllTest() {
        Mockito
                .when(repository.findAll())
                .thenReturn((List.of(getUser())));

        List<UserDto> result = userService.getAll();

        Assertions.assertEquals(1, result.get(0).getId());
        Assertions.assertEquals("name", result.get(0).getName());
        Assertions.assertEquals("asd@mail.ru", result.get(0).getEmail());
    }

    @Test
    public void getByIdTest() {
        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(getUser()));

        UserDto result = userService.getById(1);

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("name", result.getName());
        Assertions.assertEquals("asd@mail.ru", result.getEmail());
    }

    @Test
    public void createTest() {
        Mockito
                .when(repository.save(Mockito.any(User.class)))
                .thenReturn(getUser());

        UserDto result = userService.create(getUserDto());

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("name", result.getName());
        Assertions.assertEquals("asd@mail.ru", result.getEmail());
    }

    @Test
    public void createUserWithDuplicateEmailTest() {
        Mockito
                .when(repository.save(Mockito.any(User.class)))
                .thenThrow(new DuplicateEmailException("User with email=email2@mail.ru already exists"));

        final DuplicateEmailException exception = Assertions.assertThrows(
                DuplicateEmailException.class,
                () -> userService.create(getUserDto()));

        Assertions.assertEquals("User with email=email2@mail.ru already exists", exception.getMessage());
    }

    @Test
    public void getNotExistsUserByIdTest() {
        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenThrow(new UserNotFoundException("User with ID=1 not found"));

        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> userService.getById(1));

        Assertions.assertEquals("User with ID=1 not found", exception.getMessage());
    }

    @Test
    public void updateUserTest() {
        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenThrow(new IllegalUserException("User with ID=1 does not have access to user with ID=2"));

        final IllegalUserException exception = Assertions.assertThrows(
                IllegalUserException.class,
                () -> userService.update(getUserDto(), 1L));

        Assertions.assertEquals("User with ID=1 does not have access to user with ID=2", exception.getMessage());
    }
}
