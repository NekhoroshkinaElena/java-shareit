package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User add(User user);

    User getById(long id);

    User update(User user, long id);

    List<User> getAll();

    void delete(long id);
}
