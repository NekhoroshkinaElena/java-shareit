package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
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
    private ItemRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    User user = new User(1L, "user", "descr");
    User requestor = new User(2L, "requestor", "descr");
    User owner = new User(3L, "owner", "descr");

    ItemRequestDtoInput itemRequestDtoInput = new ItemRequestDtoInput();

    ItemRequestDtoOutput itemRequestDtoOutput = new ItemRequestDtoOutput(1L, "desc", LocalDateTime.now());

    ItemRequest itemRequest = new ItemRequest("request1");
    ItemRequest itemRequest2 = new ItemRequest("request2");

    Item item = new Item(1L, "item", "desc", true, owner, itemRequest);
    Item item2 = new Item(2L, "item2", "desc2", true, owner, itemRequest2);

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
    public void findAllOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestorIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(itemRequest, itemRequest2));

        List<ItemRequestDtoOutput> itemRequests = requestService.findAllOwner(user.getId());
        assertEquals(itemRequests.size(), 2);
        assertEquals(itemRequests.get(0).getId(), itemRequest.getId());
        assertEquals(itemRequests.get(0).getDescription(), itemRequest.getDescription());
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

    @Test
    public void findByIdNotFoundUser() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                requestService.findById(1L, 1L));
        assertEquals(notFoundException.getMessage(), "пользователь не найден");
    }

    @Test
    public void requestMapperToItemRequestDtoOutput() {
        itemRequest.setId(1L);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("desc");

        ItemRequestDtoOutput itemRequestDtoOutput1 = ItemRequestMapper.toItemRequestDtoOutput(itemRequest);

        assertEquals(itemRequestDtoOutput1.getId(), itemRequest.getId());
        assertEquals(itemRequestDtoOutput1.getDescription(), itemRequest.getDescription());
        assertEquals(itemRequestDtoOutput1.getCreated(), itemRequest.getCreated());
        assertEquals(itemRequest.getRequestor(), user);
    }

    @Test
    public void requestMapperToRequest() {
        ItemRequest request1 = ItemRequestMapper.toItemRequest(itemRequestDtoInput);
        assertEquals(request1.getDescription(), itemRequestDtoInput.getDescription());
    }
}
