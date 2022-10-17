package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@Slf4j
@Repository
public class UserRepositoryInMemory {

    private HashMap<Long, User> users = new HashMap<>();
    private long uniqueId = 1;

    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    public User getById(long id) {
        return users.get(id);
    }

    public User add(User user) {
        user.setId(getUniqueId());
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user, long id) {
        User userUpdate = users.get(id);
        if (user.getEmail() != null) {
            userUpdate.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userUpdate.setName(user.getName());
        }
        return userUpdate;
    }

    public void delete(long id) {
        users.remove(id);
    }

    public long getUniqueId() {
        return uniqueId++;
    }
}
