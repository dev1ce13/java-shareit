package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService service;

    @GetMapping
    public List<UserDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable long userId) {
        return service.getById(userId);
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        return service.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto,
                          @PathVariable long userId) {
        return service.update(userDto, userId);
    }

    @DeleteMapping("{userId}")
    public void deleteById(@PathVariable long userId) {
        service.deleteById(userId);
    }
}
