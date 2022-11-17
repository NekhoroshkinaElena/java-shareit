package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    public void throwIfItemNotFound() {
        when(itemRepository.findById(anyLong())).thenThrow(new NotFoundException("Вещь с id " + 2L + " не найдена."));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.getById(2L, 2L));
        assertEquals("Вещь с id " + 2L + " не найдена.", exception.getMessage());
    }

    @Test
    public void throwIfUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException("Пользователя c идентификатором " + 1L + " не найден."));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.create(
                        new ItemDto(1L, "name", "description", true, 0), 2L));
        assertEquals("Пользователя c идентификатором " + 1L + " не найден.", exception.getMessage());
    }
}
