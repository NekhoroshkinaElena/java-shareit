package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@Transactional
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplIntegrationTest {

    private final EntityManager em;
    private final UserService userService;

    @Test
    public void userSave() {
        UserDto userDto = new UserDto(1L, "name", "email@ya.ru");
        userService.add(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();
        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void getUserById() {
        UserDto userDto = new UserDto(1L, "name", "email@ya.ru");
        UserDto createdUser = userService.add(userDto);
        UserDto userFromGet = userService.getById(createdUser.getId());

        assertThat(userFromGet, notNullValue());
        assertThat(userFromGet.getName(), equalTo(userDto.getName()));
        assertThat(userFromGet.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void userUpdate() {
        UserDto userDto = new UserDto(1L, "name", "email@ya.ru");
        userService.add(userDto);
        UserDto userUpdate = new UserDto(1L, "updateName", "updateEmail@ya.ru");
        userService.update(userUpdate, 1L);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", userDto.getId()).getSingleResult();

        assertThat(user.getId(), equalTo(userUpdate.getId()));
        assertThat(user.getName(), equalTo(userUpdate.getName()));
        assertThat(user.getEmail(), equalTo(userUpdate.getEmail()));
    }

    @Test
    public void getAllUsers() {
        List<UserDto> sourceUsers = List.of(
                new UserDto(1L, "user1", "user1@ya.ry"),
                new UserDto(2L, "user2", "user2@ya.ry"),
                new UserDto(3L, "user3", "user3@ya.ry")
        );

        for (UserDto user : sourceUsers) {
            userService.add(user);
        }

        List<UserDto> targetUsers = userService.getAll();

        assertThat(targetUsers, hasSize(sourceUsers.size()));

        for (UserDto sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", equalTo(sourceUser.getId())),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    @Test
    public void deleteUser() {
        UserDto userDto = new UserDto(1L, "name", "email@ya.ru");
        userService.add(userDto);

        userService.delete(userDto.getId());

        List<UserDto> users = userService.getAll();

        assertThat(users.size(), equalTo(0));
    }
}
