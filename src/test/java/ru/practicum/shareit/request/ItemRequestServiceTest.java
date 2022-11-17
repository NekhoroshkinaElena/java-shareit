package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @InjectMocks
    private ItemRequestServiceImpl requestService;
    @Mock
    private UserRepository userRepository;

    User user = new User(1L, "user", "descr");

    ItemRequestDtoInput itemRequestDtoInput = new ItemRequestDtoInput();

    @Test
    public void addRequestWithNotFoundUser() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                requestService.add(itemRequestDtoInput, user.getId()));
        assertEquals(notFoundException.getMessage(),
                "Пользователь c идентификатором " + user.getId() + " не найден.");
    }

    @Test
    public void addRequest() {
        itemRequestDtoInput.setDescription("description");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        ItemRequestDtoOutput itemRequest = requestService.add(
                itemRequestDtoInput, user.getId());

        assertEquals(itemRequest.getDescription(),
                itemRequestDtoInput.getDescription());
    }

    @Test
    public void findAllOwnerWithNotFoundUser() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                requestService.findAllOwner(user.getId()));
        assertEquals(notFoundException.getMessage(), "пользователь не найден");
    }

    @Test
    public void findAllWithNotFoundUser() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                requestService.findAll(0, 20, user.getId()));
        assertEquals(notFoundException.getMessage(), "пользователь не найден");
    }

    @Test
    public void findAllWithWrongParam() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        ValidationException exception = assertThrows(ValidationException.class, () ->
                requestService.findAll(0, -1, user.getId()));
        assertEquals(exception.getMessage(), "параметры не могут быть отрицательными");
    }

    @Test
    public void findAllWithEmptyParam() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        ValidationException exception = assertThrows(ValidationException.class, () ->
                requestService.findAll(0, 0, user.getId()));
        assertEquals(exception.getMessage(), "параметры не могут быть пустыми");
    }

    @Test
    public void findByIdNotFoundRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                requestService.findById(1L, 1L));
        assertEquals(notFoundException.getMessage(), "запрос с id 1 не существует");
    }
}
