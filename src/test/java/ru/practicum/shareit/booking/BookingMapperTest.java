package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class BookingMapperTest {
    private Booking booking;
    private BookingResponseDto bookingResponseDto;
    private BookingShortResponseDto bookingShort;
    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    void setUp() {
        Item item = Item.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        bookingRequestDto = BookingRequestDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();

        User user = User.builder()
                .id(1L)
                .name("Name")
                .email("some@email.ru")
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(3))
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();

        bookingResponseDto = BookingMapper.toBookingResponseDto(booking);
        bookingShort = BookingMapper.bookingToDtoId(booking);
    }

    @Test
    void testToBookingResponse() {
        assertNotNull(booking);
        assertEquals(booking.getId(), bookingResponseDto.getId());
        assertEquals(booking.getItem().getId(), bookingResponseDto.getItem().getId());
        assertEquals(booking.getStart(), bookingResponseDto.getStart());
        assertEquals(booking.getEnd(), bookingResponseDto.getEnd());
        assertEquals(booking.getBooker().getId(), bookingResponseDto.getBooker().getId());
        assertEquals(booking.getStatus(), bookingResponseDto.getStatus());
    }

    @Test
    void testBookingToDto() {
        assertNotNull(booking);
        assertEquals(booking.getId(), bookingShort.getId());
        assertEquals(booking.getBooker().getId(), bookingShort.getBookerId());
    }

    @Test
    void testRequestToBooking() {
        booking = BookingMapper.requestToBooking(bookingRequestDto);
        assertNotNull(bookingRequestDto);
        assertEquals(bookingRequestDto.getStart(), booking.getStart());
        assertEquals(bookingRequestDto.getEnd(), booking.getEnd());
    }
}
