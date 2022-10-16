package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User addUser(User user);

    User getUser(long id);

    User userUpdate(User user, long id);

    List<User> getUsers();

    void deleteUser(long id);
}
