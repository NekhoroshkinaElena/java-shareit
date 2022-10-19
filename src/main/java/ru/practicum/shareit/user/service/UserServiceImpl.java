package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserRepositoryInMemory;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepositoryInMemory userRepository;

    public User add(User user) {
        throwIfUserAlreadyExist(user);
        return userRepository.add(user);
    }

    public User getById(long id) {
        throwIfUserNotFound(id);
        return userRepository.getById(id);
    }

    public User update(User user, long id) {
        throwIfUserNotFound(id);
        throwIfUserAlreadyExist(user);
        return userRepository.update(user, id);
    }

    public List<User> getAll() {
        return userRepository.getAll();
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
