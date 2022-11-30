package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto add(UserDto user);

    UserDto getById(long id);

    UserDto update(UserDto userDto, long id);

    List<UserDto> getAll();

    void delete(long id);
}
