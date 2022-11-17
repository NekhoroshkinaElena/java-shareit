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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    @Test
    void addUser() {
        User user = new User(1L, "UserName", "user@mail.ru");
        UserDto userDto = new UserDto(1L, "UserName", "user@mail.ru");

        when(userRepository.save(any())).thenReturn(user);
        UserDto result = userService.add(userDto);

        assertEquals(result, userDto);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void getUserWithWrongId() {
        UserDto userDto = new UserDto(10L, "UserName", "");
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                userService.getById(userDto.getId()));
        assertEquals(notFoundException.getMessage(), "Пользователя c идентификатором "
                + userDto.getId() + " не существует.");
    }

    @Test
    public void throwIfUserWithEmailAlreadyExist() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User(1L, "name", "email")));
        when(userRepository.findAll())
                .thenThrow(new AlreadyExistsException("Пользователь с такой почтой уже добавлен."));

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class,
                () -> userService.update(new UserDto(1L, "name", "email@ya.ru"), 2L));
        assertEquals("Пользователь с такой почтой уже добавлен.", exception.getMessage());
    }
}
