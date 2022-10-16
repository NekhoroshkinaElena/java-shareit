package ru.practicum.shareit.user;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@Slf4j
@Repository
public class UserRepositoryInMemory {

    private HashMap<Long, User> users = new HashMap<>();
    private long uniqueId = 1;

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUser(long id) {
        return users.get(id);
    }

    public User addUser(User user) {
        for (User u : users.values()) {
            if (u.getEmail().equals(user.getEmail())) {
                log.error("Пользователь с таким email уже существует");
                throw new RuntimeException("Пользователь с таким email уже существует");
            }
        }
        User user1 = new User(getUniqueId(), user.getName(), user.getEmail());
        users.put(user1.getId(), user1);
        return user1;
    }

    public User userUpdate(User user, long id) {
        User userUpdate = users.get(id);
        if (user.getEmail() != null) {
            for (User u : users.values()) {
                if (u.getEmail().equals(user.getEmail())) {
                    log.error("Пользователь с таким email уже существует");
                    throw new RuntimeException("Пользователь с таким email уже существует");
                }
            }
            userUpdate.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userUpdate.setName(user.getName());
        }
        return userUpdate;
    }

    public void deleteUser(long id) {
        users.remove(id);
    }

    public long getUniqueId() {
        return uniqueId++;
    }
}
