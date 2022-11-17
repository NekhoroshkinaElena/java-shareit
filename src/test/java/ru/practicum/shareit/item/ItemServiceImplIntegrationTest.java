package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDtoOutput;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@Transactional
//@Rollback(false)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplIntegrationTest {
    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final CommentRepository commentRepository;

    @Test
    public void create() {
        ItemDto itemDto = new ItemDto(1L, "item", "description", true, 0);
        itemService.create(itemDto, 1L);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item1 = query.setParameter("id", itemDto.getId())
                .getSingleResult();

        assertThat(item1.getId(), notNullValue());
        assertThat(item1.getName(), equalTo(itemDto.getName()));
    }

    @Test
    public void getUserById() {
        UserDto userDto = new UserDto(1L, "name", "email@ya.ru");
        UserDto createdUser = userService.add(userDto);

        ItemDto itemDto = new ItemDto(1L, "item", "description", true, 0);
        itemService.create(itemDto, 1L);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item1 = query.setParameter("id", itemDto.getId())
                .getSingleResult();

        assertThat(item1.getName(), equalTo(itemDto.getName()));
    }

    @Test
    public void getAllItems() {
        UserDto userDto = new UserDto(1L, "name", "email@ya.ru");
        UserDto createUser = userService.add(userDto);
        ItemDto itemDto = new ItemDto(1L, "item", "description", true, 0);
        ItemDto itemDto2 = new ItemDto(2L, "item2", "description2", true, 0);
        itemService.create(itemDto, createUser.getId());
        itemService.create(itemDto2, createUser.getId());

        List<ItemOutputDto> items = itemService.getAll(1, 0, 20);

        assertThat(items.size(), equalTo(2));
    }

    @Test
    public void updateItems() {
        UserDto userDto = new UserDto(1L, "name", "email@ya.ru");
        UserDto createUser = userService.add(userDto);
        ItemDto itemDto = new ItemDto(1L, "item", "description", true, 0);
        itemService.create(itemDto, createUser.getId());
        ItemDto newItem = new ItemDto(1L, "updateItem",
                "descriptionUpdate", true, 0);
        itemService.update(newItem, itemDto.getId(), createUser.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item1 = query.setParameter("id", itemDto.getId())
                .getSingleResult();

        assertThat(item1.getId(), equalTo(itemDto.getId()));
        assertThat(item1.getName(), equalTo(newItem.getName()));
        assertThat(item1.getDescription(), equalTo(newItem.getDescription()));
    }

    @Test
    public void searchItem() {
        UserDto userDto = new UserDto(1L, "name", "email@ya.ru");
        UserDto createUser = userService.add(userDto);
        ItemDto itemDto = new ItemDto(1L, "item", "description", true, 0);
        ItemDto itemDto2 = new ItemDto(2L, "item2", "description2", true, 0);
        itemService.create(itemDto, createUser.getId());
        itemService.create(itemDto2, createUser.getId());

        List<ItemDto> itemDtoSearch = itemService.search("iTem", 0, 20);

        assertThat(itemDtoSearch.size(), equalTo(2));
    }

    @Test
    public void addComment() {
        UserDto userDto = new UserDto(1L, "name", "email@ya.ru");
        UserDto createUser = userService.add(userDto);

        UserDto userDto2 = new UserDto(2L, "name2", "email2@ya.ru");
        UserDto createUser2 = userService.add(userDto2);

        ItemDto itemDto = new ItemDto(1L, "item", "description", true, 0);
        itemService.create(itemDto, createUser.getId());

        BookingDtoOutput bookingDtoOutput = bookingService.save(new BookingDtoInput(itemDto.getId(),
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3)), createUser2.getId());

        bookingService.approve(userDto.getId(), bookingDtoOutput.getId(), true);

        Comment comment = new Comment("text", LocalDateTime.now().plusDays(5));
        comment.setItem(ItemMapper.toItem(itemDto));
        comment.setAuthor(UserMapper.toUser(userDto2));
        comment.setAuthor(UserMapper.toUser(userDto2));
        commentRepository.save(comment);

        List<CommentDtoOutput> comments = itemService.getById(itemDto.getId(), userDto.getId())
                .getComments();

        assertThat(comments.size(), equalTo(1));
        assertThat(comments.get(0).getText(), equalTo("text"));
    }
}
