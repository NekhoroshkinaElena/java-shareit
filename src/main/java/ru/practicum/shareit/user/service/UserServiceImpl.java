package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserRepositoryInMemory;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepositoryInMemory userRepository;

    public User add(User user) {
        for (User u : userRepository.getAll()) {
            if (Objects.equals(user.getEmail(), u.getEmail())) {
                log.error("Пользователь с таким email уже существует");
                throw new RuntimeException("Пользователь с таким email уже существует");
            }
        }
        return userRepository.add(user);
    }

    public User getById(long id) {
        if (userRepository.getById(id) == null) {
            log.error("пользователь не найден");
            throw new NotFoundException("пользователь не найден");
        }
        return userRepository.getById(id);
    }

    public User update(User user, long id) {
        if (userRepository.getById(id) == null) {
            log.error("пользователь не найден");
            throw new NotFoundException("пользователь не найден");
        }
        for (User u : userRepository.getAll()) {
            if (u.getEmail().equals(user.getEmail())) {
                log.error("Пользователь с таким email уже существует");
                throw new RuntimeException("Пользователь с таким email уже существует");
            }
        }
        return userRepository.update(user, id);
    }

    public List<User> getAll() {
        return userRepository.getAll();
    }

    public void delete(long id) {
        userRepository.delete(id);
    }
}
