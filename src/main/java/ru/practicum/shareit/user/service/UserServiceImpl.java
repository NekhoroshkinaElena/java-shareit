package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserRepositoryInMemory;
import ru.practicum.shareit.user.model.User;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepositoryInMemory userRepository;

    public User addUser(User user) {
        return userRepository.addUser(user);
    }

    public User getUser(long id) {
        return userRepository.getUser(id);
    }

    public User userUpdate(User user, long id) {
        return userRepository.userUpdate(user, id);
    }

    public List<User> getUsers() {
        return userRepository.getUsers();
    }

    public void deleteUser(long id) {
        userRepository.deleteUser(id);
    }
}
