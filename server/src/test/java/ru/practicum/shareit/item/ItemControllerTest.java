package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.CommentDtoOutput;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mvc;

    private final User user = new User(1L,
            "user",
            "email@ya.ru");

    Item item = new Item(1L,
            "item",
            "description",
            true,
            user,
            null);

    Item item2 = new Item(2L,
            "item2",
            "description2",
            true,
            user,
            null);

    Item item3 = new Item(3L,
            "item3",
            "description3",
            true,
            user,
            null);

    CommentDtoOutput comment = new CommentDtoOutput(1L,
            "comment",
            "author",
            LocalDateTime.now().minusDays(1));

    @Test
    public void createItem() throws Exception {
        when(itemService.create(any(), anyLong()))
                .thenReturn(ItemMapper.toItemDto(item));

        mvc.perform(post("/items", ItemMapper.toItemDto(item), 1L)
                        .content(mapper.writeValueAsString(ItemMapper.toItemDtoOutput(item)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));
    }

    @Test
    public void createItemWhenShouldThrowNotFoundException() throws Exception {
        when(itemService.create(any(), anyLong()))
                .thenThrow(new NotFoundException(""));

        mvc.perform(post("/items", ItemMapper.toItemDto(item), 1L)
                        .content(mapper.writeValueAsString(ItemMapper.toItemDtoOutput(item)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateItem() throws Exception {
        when(itemService.update(any(), anyLong(), anyLong()))
                .thenReturn(ItemMapper.toItemDto(item));

        mvc.perform(patch("/items/" + 2L)
                        .content(mapper.writeValueAsString(ItemMapper.toItemDtoOutput(item)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));
    }

    @Test
    public void updateItemWhenShouldThrowNotFoundException() throws Exception {
        when(itemService.update(any(), anyLong(), anyLong()))
                .thenThrow(new NotFoundException(""));

        mvc.perform(patch("/items/" + 2L)
                        .content(mapper.writeValueAsString(ItemMapper.toItemDtoOutput(item)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getItemById() throws Exception {
        when(itemService.getById(anyLong(), anyLong()))
                .thenReturn(ItemMapper.toItemDtoOutput(item));

        mvc.perform(get("/items/" + 2L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));
    }

    @Test
    public void getByIdWhenShouldThrowNotFoundException() throws Exception {
        when(itemService.getById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException(""));

        mvc.perform(get("/items/" + 2L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllItems() throws Exception {
        List<ItemOutputDto> items = (List.of(item, item2, item3)).stream()
                .map(ItemMapper::toItemDtoOutput).collect(Collectors.toList());

        when(itemService.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(items);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(item2.getId()), Long.class))
                .andExpect(jsonPath("$[2].id", is(item3.getId()), Long.class));
    }

    @Test
    public void getAllItemsWhenShouldThrowNotFoundException() throws Exception {
        when(itemService.getAll(anyLong(), anyInt(), anyInt()))
                .thenThrow(new NotFoundException(""));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void searchItems() throws Exception {
        when(itemService.search(anyString(), anyInt(), anyInt()))
                .thenReturn(Stream.of(item, item2, item3).map(ItemMapper::toItemDto).collect(Collectors.toList()));

        mvc.perform(get("/items/search")
                        .param("text", "item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(item2.getId()), Long.class))
                .andExpect(jsonPath("$[2].id", is(item3.getId()), Long.class));
    }

    @Test
    public void addComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(comment);

        mvc.perform(post("/items/" + 1L + "/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.authorName", is(comment.getAuthorName())));
    }

    @Test
    public void addCommentWhenShouldThrowNotFoundException() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any())).thenThrow(new NotFoundException(""));

        mvc.perform(post("/items/" + 1L + "/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
