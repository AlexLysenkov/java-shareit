package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;
    private BookingRequestDto bookingRequestDto;
    private BookingResponseDto bookingResponseDto;

    @BeforeEach
    void setUp() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Name")
                .email("some@email.ru")
                .build();

        ItemShortResponseDto itemShortResponseDto = ItemShortResponseDto.builder()
                .id(1L)
                .name("Item")
                .build();

        bookingRequestDto = BookingRequestDto.builder()
                .itemId(itemShortResponseDto.getId())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();

        bookingResponseDto = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(3))
                .item(itemShortResponseDto)
                .booker(userDto)
                .status(Status.APPROVED)
                .build();
    }

    @Test
    void testCreateBooking() throws Exception {
        Mockito
                .when(bookingService.createBooking(Mockito.any(), Mockito.anyLong())).thenReturn(bookingResponseDto);
        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingResponseDto)));
    }

    @Test
    void testUpdateBooking() throws Exception {
        Mockito
                .when(bookingService.updateBooking(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(bookingResponseDto);
        mvc.perform(patch("/bookings/{bookingId}?approved=true", 1L)
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingResponseDto)));
    }

    @Test
    void testGetBookingById() throws Exception {
        Mockito
                .when(bookingService.getBookingById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(bookingResponseDto);
        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingResponseDto)));
    }

    @Test
    void testGetAllBookingsByUserId() throws Exception {
        Mockito
                .when(bookingService.getAllBookingsByUserId(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(),
                        Mockito.anyInt())).thenReturn(List.of(bookingResponseDto));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingResponseDto.getId()))
                .andExpect(jsonPath("$[0].status").value(bookingResponseDto.getStatus().toString()))
                .andExpect(jsonPath("$[0].item.id").value(bookingResponseDto.getItem().getId()))
                .andDo(print());
        Mockito.verify(bookingService).getAllBookingsByUserId(1L, "ALL", 0, 10);
    }

    @Test
    void testGetAllBookingsByOwnerId() throws Exception {
        Mockito
                .when(bookingService.getAllBookingsByOwnerId(Mockito.anyLong(), Mockito.anyString(),
                        Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of(bookingResponseDto));
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingResponseDto.getId()))
                .andExpect(jsonPath("$[0].status").value(bookingResponseDto.getStatus().toString()))
                .andExpect(jsonPath("$[0].item.id").value(bookingResponseDto.getItem().getId()))
                .andDo(print());
        Mockito.verify(bookingService).getAllBookingsByOwnerId(1L, "ALL", 0, 10);
    }
}
