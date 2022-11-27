package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@Transactional
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTest {
    private final EntityManager em;
    private final ItemRequestService requestService;
    private final UserService userService;
    private final ItemService itemService;

    private final User user = new User(1L,
            "name",
            "email@ya.ru");

    private final User user2 = new User(2L,
            "name2",
            "email2@ya.ru");

    private final User user3 = new User(3L,
            "name3",
            "email3@ya.ru");

    private final ItemRequestDtoInput itemRequestDtoInput = new ItemRequestDtoInput();
    private final ItemRequestDtoInput itemRequestDtoInput2 = new ItemRequestDtoInput();

    private final ItemDto itemDto = new ItemDto(1L,
            "item",
            "desc",
            true,
            1L);

    private final ItemDto itemDto2 = new ItemDto(2L,
            "item",
            "desc",
            true,
            2L);

    @BeforeEach
    public void setUp() {
        itemRequestDtoInput.setDescription("description");
        itemRequestDtoInput2.setDescription("description2");
        userService.add(UserMapper.toUserDto(user));
    }

    @Test
    public void saveRequest() {
        requestService.add(itemRequestDtoInput, user.getId());

        TypedQuery<ItemRequest> query = em.createQuery("SELECT r from ItemRequest r where r.id = :id",
                ItemRequest.class);

        ItemRequest itemRequest = query.setParameter("id", 1L)
                .getSingleResult();
        assertThat(itemRequest.getId(), equalTo(1L));
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDtoInput.getDescription()));
    }

    @Test
    public void findAllOwner() {
        requestService.add(itemRequestDtoInput, user.getId());
        itemService.create(itemDto, user.getId());

        requestService.add(itemRequestDtoInput2, user.getId());
        itemService.create(itemDto2, user.getId());

        List<ItemRequestDtoOutput> requests = requestService.findAllOwner(user.getId());

        assertThat(requests.size(), equalTo(2));
        assertThat(requests.get(0).getDescription(), equalTo(itemRequestDtoInput2.getDescription()));
        assertTrue(requests.get(0).getItems().contains(itemDto2));
        assertThat(requests.get(1).getDescription(), equalTo(itemRequestDtoInput.getDescription()));
        assertTrue(requests.get(1).getItems().contains(itemDto));
    }

    @Test
    public void findAll() {
        userService.add(UserMapper.toUserDto(user2));
        userService.add(UserMapper.toUserDto(user3));

        requestService.add(itemRequestDtoInput, user.getId());
        itemService.create(itemDto, user.getId());

        requestService.add(itemRequestDtoInput2, user.getId());
        itemService.create(itemDto2, user2.getId());

        List<ItemRequestDtoOutput> requests = requestService.findAll(0, 20, user3.getId());

        assertThat(requests.size(), equalTo(2));
        assertThat(requests.get(0).getDescription(), equalTo(itemRequestDtoInput2.getDescription()));
        assertTrue(requests.get(0).getItems().contains(itemDto2));
        assertThat(requests.get(1).getDescription(), equalTo(itemRequestDtoInput.getDescription()));
        assertTrue(requests.get(1).getItems().contains(itemDto));
    }

    @Test
    public void findById() {
        itemRequestDtoInput.setDescription("description");
        requestService.add(itemRequestDtoInput, user.getId());

        ItemRequestDtoOutput itemRequestDtoOutput = requestService.findById(1L, user.getId());

        assertThat(itemRequestDtoOutput.getDescription(), equalTo(
                itemRequestDtoInput.getDescription()));
    }
}
