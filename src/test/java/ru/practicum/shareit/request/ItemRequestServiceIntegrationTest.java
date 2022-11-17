package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
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

@Transactional
@Rollback(false)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTest {
    private final EntityManager em;
    private final ItemRequestService requestService;
    private final UserService userService;

    private User user = new User(1L, "name", "email@ya.ru");
    private User user2 = new User(2L, "name2", "email2@ya.ru");
    private ItemRequestDtoInput itemRequestDtoInput = new ItemRequestDtoInput();
    private ItemRequestDtoInput itemRequestDtoInput2 = new ItemRequestDtoInput();

    @Test
    public void saveRequest() {
        userService.add(UserMapper.toUserDto(user));
        itemRequestDtoInput.setDescription("description");
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
        userService.add(UserMapper.toUserDto(user));
        requestService.add(itemRequestDtoInput2, user.getId());

        List<ItemRequestDtoOutput> requests = requestService.findAllOwner(user.getId());

        System.out.println(requests);
    }

    @Test
    public void findAll() {
        userService.add(UserMapper.toUserDto(user));
        System.out.println(requestService.findAll(0, 20, 1L));
    }

    @Test
    public void findById() {
        userService.add(UserMapper.toUserDto(user));
        itemRequestDtoInput.setDescription("description");
        requestService.add(itemRequestDtoInput, user.getId());

        ItemRequestDtoOutput itemRequestDtoOutput = requestService.findById(1L, user.getId());

        assertThat(itemRequestDtoOutput.getDescription(), equalTo(
                itemRequestDtoInput.getDescription()));
    }
}
