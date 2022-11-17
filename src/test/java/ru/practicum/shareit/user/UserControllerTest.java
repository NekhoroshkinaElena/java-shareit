package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mvc;

    private UserDto userDto = new UserDto(
            1L,
            "name",
            "email@ya.ru");

    @Test
    void saveUser() throws Exception {
        when(userService.add(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void saveUserWithEmptyEmail() throws Exception {
        when(userService.add(any()))
                .thenThrow(new ValidationException("Укажите адрес электронной почты"));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Укажите адрес электронной почты")));
    }

    @Test
    void saveUserWithAlreadyExistEmail() throws Exception {
        when(userService.add(any()))
                .thenThrow(new ValidationException("Пользователь с такой электронной почтой уже существует"));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error",
                        is("Пользователь с такой электронной почтой уже существует")));
    }

    @Test
    void getUserId() throws Exception {
        long id = 1L;
        when(userService.getById(id))
                .thenReturn(userDto);
        mvc.perform(get("/users/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void updateUserWithAlreadyExistEmail() throws Exception {
        when(userService.update(any(), anyLong()))
                .thenThrow(new AlreadyExistsException("пользователь с такой почтой уже существует"));

        mvc.perform(patch("/users/" + userDto.getId()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getUserWithWrongId() throws Exception {
        when(userService.getById(anyLong())).thenThrow(new NotFoundException("Неверный id"));

        mvc.perform(get("/users/" + 1L))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void userUpdate() throws Exception {
        UserDto userDtoUpdate = new UserDto(1L, "name", "email@ya.ru");


        when(userService.update(userDtoUpdate, userDto.getId()))
                .thenReturn(userDtoUpdate);

        mvc.perform(patch("/users/" + userDto.getId())
                        .content(mapper.writeValueAsString(userDtoUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoUpdate.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoUpdate.getName())))
                .andExpect(jsonPath("$.email", is(userDtoUpdate.getEmail())));
    }

    @Test
    void getAllUsers() throws Exception {
        when(userService.getAll())
                .thenReturn(List.of(new UserDto(1, "name", "email@ya.ru"),
                        new UserDto(2, "name2", "email2@ya.ru"),
                        new UserDto(3, "name3", "email3@ya.ru")
                ));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[2].id", is(3)))
                .andExpect(jsonPath("$[0].name", is("name")))
                .andExpect(jsonPath("$[1].name", is("name2")))
                .andExpect(jsonPath("$[2].name", is("name3")))
                .andExpect(jsonPath("$[0].email", is("email@ya.ru")))
                .andExpect(jsonPath("$[1].email", is("email2@ya.ru")))
                .andExpect(jsonPath("$[2].email", is("email3@ya.ru")));
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/" + 1L))
                .andExpect(status().isOk());
    }
}
