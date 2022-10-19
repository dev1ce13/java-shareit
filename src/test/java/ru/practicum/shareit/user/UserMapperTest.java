package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

public class UserMapperTest {

    @Test
    public void mapToUserTest() {
        var userDto = UserDto.builder().id(1).name("name").email("111@mail.ru").build();

        User result = UserMapper.mapToUser(userDto);

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("name", result.getName());
        Assertions.assertEquals("111@mail.ru", result.getEmail());
    }

    @Test
    public void mapToUserDtoTest() {
        var user = new User(1, "name", "111@mail.ru");

        UserDto result = UserMapper.mapToUserDto(user);

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("name", result.getName());
        Assertions.assertEquals("111@mail.ru", result.getEmail());
    }
}
