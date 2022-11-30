package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestsControllerTest {
    @MockBean
    private ItemRequestServiceImpl itemRequestService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final ItemRequestDtoOutput itemRequestDto = new ItemRequestDtoOutput(1,
            "des",
            LocalDateTime.now().plusDays(1));

    @Test
    public void addRequest() throws Exception {
        when(itemRequestService.add(any(), anyLong())).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    public void addRequestWhenObjectNotFound() throws Exception {
        when(itemRequestService.add(any(), anyLong())).thenThrow(new NotFoundException(""));

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findAllOwnerRequests() throws Exception {
        when(itemRequestService.findAllOwner(anyLong())).thenReturn(List.of(
                new ItemRequestDtoOutput(1, "des1", LocalDateTime.of(
                        2022, 11, 18, 12, 0, 0)),
                new ItemRequestDtoOutput(2, "des2", LocalDateTime.of(
                        2022, 12, 18, 12, 0, 0))
        ));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[1].id").value(2));
    }

    @Test
    public void findAllOwnerRequestsWhenUserNotFound() throws Exception {
        when(itemRequestService.findAllOwner(anyLong())).thenThrow(new NotFoundException(""));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findAllRequests() throws Exception {
        when(itemRequestService.findAll(anyInt(), anyInt(), anyLong())).thenReturn(List.of(
                new ItemRequestDtoOutput(1, "des1", LocalDateTime.of(
                        2022, 11, 18, 12, 0, 0)),
                new ItemRequestDtoOutput(2, "des2", LocalDateTime.of(
                        2022, 12, 18, 12, 0, 0))
        ));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[1].id").value(2));
    }

    @Test
    public void findAllRequestsWhenShouldThrowValidationException() throws Exception {
        when(itemRequestService.findAll(anyInt(), anyInt(), anyLong())).thenThrow(new ValidationException(""));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void findAllRequestsWhenObjectNotFound() throws Exception {
        when(itemRequestService.findAll(anyInt(), anyInt(), anyLong())).thenThrow(new NotFoundException(""));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findRequestById() throws Exception {
        when(itemRequestService.findById(anyLong(), anyLong()))
                .thenReturn(new ItemRequestDtoOutput(1, "desc", LocalDateTime.now()));

        mvc.perform(get("/requests/" + 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    public void findRequestByIdWhenObjectNotFound() throws Exception {
        when(itemRequestService.findById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException(""));

        mvc.perform(get("/requests/" + 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }
}
