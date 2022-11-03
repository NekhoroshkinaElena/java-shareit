package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


    public UserDto add(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    public UserDto getById(long id) {
        throwIfUserNotFound(id);
        User user = userRepository.findById(id).get();
        return UserMapper.toUserDto(user);
    }

    public UserDto update(UserDto userDto, long id) {
        throwIfUserNotFound(id);
        User updateUser = userRepository.findById(id).get();
        if (userDto.getName() != null) {
            updateUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            if (userRepository.findAll().stream()
                    .anyMatch(u -> u.getEmail().toLowerCase(Locale.ROOT).equals(userDto.getEmail().toLowerCase()))) {
                throw new AlreadyExistsException("Пользователь с почтой " + userDto.getEmail() + " уже добавлен.");
            }
            updateUser.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(updateUser));
    }

    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public void delete(long id) {
        userRepository.deleteById(id);
    }

    public void throwIfUserNotFound(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.error("пользователя c идентификатором " + userId + " не существует");
            throw new NotFoundException("пользователя c идентификатором " + userId + " не существует");
        }
    }
}
