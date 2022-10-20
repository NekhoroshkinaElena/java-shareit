package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepositoryInMemory;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepositoryInMemory userRepository;

    public UserDto add(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        throwIfUserAlreadyExist(user);
        return UserMapper.toUserDto(userRepository.add(user));
    }

    public UserDto getById(long id) {
        throwIfUserNotFound(id);
        return UserMapper.toUserDto(userRepository.getById(id));
    }

    public UserDto update(UserDto userDto, long id) {
        User user = UserMapper.toUser(userDto);
        throwIfUserNotFound(id);
        throwIfUserAlreadyExist(user);
        return UserMapper.toUserDto(userRepository.update(user, id));
    }

    public List<UserDto> getAll() {
        return userRepository.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public void delete(long id) {
        userRepository.delete(id);
    }

    public void throwIfUserNotFound(long userId) {
        if (userRepository.getById(userId) == null) {
            log.error("пользователя c идентификатором " + userId + " не существует");
            throw new NotFoundException("пользователя c идентификатором " + userId + " не существует");
        }
    }

    public void throwIfUserAlreadyExist(User user) {
        for (User u : userRepository.getAll()) {
            if (u.getEmail().equals(user.getEmail())) {
                log.error("Пользователь с email " + user.getEmail() + " уже существует");
                throw new AlreadyExistsException("Пользователь с email " + user.getEmail() + " уже существует");
            }
        }
    }
}
