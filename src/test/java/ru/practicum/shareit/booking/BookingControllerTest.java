package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;

    private final BookingDtoOutput bookingDtoOutput = new BookingDtoOutput(
            1L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(3),
            BookingStatus.APPROVED,
            new User(1L, "name", "email@ya.ru"),
            new ItemDtoForBooking(1L, "name")
    );

    private final BookingDtoOutput bookingDtoOutput2 = new BookingDtoOutput(
            2L,
            LocalDateTime.now().plusDays(2),
            LocalDateTime.now().plusDays(4),
            BookingStatus.APPROVED,
            new User(2L, "name2", "email2@ya.ru"),
            new ItemDtoForBooking(2L, "name2")
    );

    private final BookingDtoInput bookingDtoInput = new BookingDtoInput(
            1L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(3));

    @Test
    void saveBooking() throws Exception {
        when(bookingService.save(any(), anyLong())).thenReturn(bookingDtoOutput);

        mvc.perform(post("/bookings", bookingDtoInput, 1L)
                        .content(mapper.writeValueAsString(bookingDtoOutput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOutput.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOutput.getItem().getId()), Long.class))
                .andExpect(jsonPath("@.booker.id", is(bookingDtoOutput.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDtoOutput.getStatus().toString())));
    }

    @Test
    void saveBookingWhenShouldThrowNotFoundException() throws Exception {
        when(bookingService.save(any(), anyLong())).thenThrow(new NotFoundException(""));

        mvc.perform(post("/bookings", bookingDtoInput)
                        .content(mapper.writeValueAsString(bookingDtoOutput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveBookingWhenShouldThrowValidationException() throws Exception {
        when(bookingService.save(any(), anyLong())).thenThrow(new ValidationException(""));

        mvc.perform(post("/bookings", bookingDtoInput, 1L)
                        .content(mapper.writeValueAsString(bookingDtoOutput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveBooking() throws Exception {
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDtoOutput);

        mvc.perform(patch("/bookings/" + bookingDtoOutput.getId())
                        .content(mapper.writeValueAsString(bookingDtoOutput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOutput.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOutput.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOutput.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void approveBookingWhenShouldThrowNotFoundException() throws Exception {
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean())).thenThrow(new NotFoundException(""));

        mvc.perform(patch("/bookings/" + bookingDtoOutput.getId())
                        .content(mapper.writeValueAsString(bookingDtoOutput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 0L)
                        .param("approved", "true"))
                .andExpect(status().isNotFound());
    }

    @Test
    void approveBookingWhenShouldThrowValidationException() throws Exception {
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean())).thenThrow(new ValidationException(""));

        mvc.perform(patch("/bookings/" + bookingDtoOutput.getId())
                        .content(mapper.writeValueAsString(bookingDtoOutput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "not"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(bookingDtoOutput);
        mvc.perform(get("/bookings/" + bookingDtoOutput.getId())
                        .param("approve", "true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOutput.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOutput.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOutput.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void getBookingByIdWhenShouldThrowNotFoundException() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException(""));
        mvc.perform(get("/bookings/" + bookingDtoOutput.getId())
                        .param("approve", "true")
                        .header("X-Sharer-User-Id", 0L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllForBooker() throws Exception {
        when(bookingService.findAllForBooker(anyInt(), anyInt(), anyLong(), anyString()))
                .thenReturn(List.of(bookingDtoOutput, bookingDtoOutput2));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "20")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id", is(bookingDtoOutput.getId()), Long.class))
                .andExpect(jsonPath("$.[1].id", is(bookingDtoOutput2.getId()), Long.class));
    }

    @Test
    void getAllForBookerWhenShouldThrowNotFoundException() throws Exception {
        when(bookingService.findAllForBooker(anyInt(), anyInt(), anyLong(), anyString()))
                .thenThrow(new NotFoundException(""));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 0L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllForBookerWhenShouldThrowValidationException() throws Exception {
        when(bookingService.findAllForBooker(anyInt(), anyInt(), anyLong(), anyString()))
                .thenThrow(new ValidationException(""));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "-20")
                        .param("state", "ALL"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllForOwner() throws Exception {
        when(bookingService.findAllForOwner(anyInt(), anyInt(), anyLong(), anyString()))
                .thenReturn(List.of(bookingDtoOutput, bookingDtoOutput2));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "20")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("@.length()").value(2))
                .andExpect(jsonPath("$.[0].id", is(bookingDtoOutput.getId()), Long.class))
                .andExpect(jsonPath("$.[1].id", is(bookingDtoOutput2.getId()), Long.class));
    }

    @Test
    void getAllForOwnerWhenShouldThrowNotFoundException() throws Exception {
        when(bookingService.findAllForOwner(anyInt(), anyInt(), anyLong(), anyString()))
                .thenThrow(new NotFoundException(""));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllForOwnerWhenShouldThrowValidationException() throws Exception {
        when(bookingService.findAllForOwner(anyInt(), anyInt(), anyLong(), anyString()))
                .thenThrow(new ValidationException(""));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "-20")
                        .param("state", "ALL"))
                .andExpect(status().isBadRequest());
    }
}
