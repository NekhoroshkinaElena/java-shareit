package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    User user = new User(1L, "UserName", "user@mail.ru");
    User user2 = new User(2L, "UserName2", "user2@mail.ru");
    UserDto userDto = new UserDto(1L, "UserName", "user@mail.ru");

    @Test
    void addUser() {
        when(userRepository.save(any())).thenReturn(user);
        UserDto result = userService.add(userDto);

        assertEquals(result, userDto);
    }

    @Test
    void getById() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        UserDto result = userService.getById(userDto.getId());

        assertEquals(result, userDto);
    }

    @Test
    void getUserByIdWithWrongId() {
        UserDto userDto = new UserDto(10L, "UserName", "");
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                userService.getById(userDto.getId()));

        assertEquals(notFoundException.getMessage(), "Пользователя c идентификатором "
                + userDto.getId() + " не существует.");
    }

    @Test
    public void updateUserWithEmailAlreadyExist() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(List.of(user));

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class,
                () -> userService.update(userDto, 2L));
        assertEquals("Пользователь с почтой " + user.getEmail() + " уже добавлен.", exception.getMessage());
    }

    @Test
    public void updateUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        when(userRepository.save(any())).thenReturn(user);

        UserDto userUpdate = userService.update(userDto, userDto.getId());

        assertEquals(userUpdate.getId(), user.getId());
        assertEquals(userUpdate.getName(), user.getName());
        assertEquals(userUpdate.getEmail(), user.getEmail());
    }

    @Test
    public void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user, user2));

        List<UserDto> users = userService.getAll();

        assertEquals(users.size(), 2);
        assertEquals(users.get(0).getId(), user.getId());
        assertEquals(users.get(1).getId(), user2.getId());
    }
}
