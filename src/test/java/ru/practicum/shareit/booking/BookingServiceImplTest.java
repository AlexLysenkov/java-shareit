package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceImplTest {
    private final EntityManager em;
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemService itemService;
    private BookingRequestDto bookingRequestDto;
    private final LocalDateTime time1 = LocalDateTime.of(2023, 11, 15, 13, 25);
    private final LocalDateTime time2 = LocalDateTime.of(2023, 12, 15, 13, 25);

    @BeforeEach
    void setUp() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Name")
                .email("some@email.ru")
                .build();
        userService.createUserDto(userDto);

        UserDto userDto1 = UserDto.builder()
                .id(2L)
                .name("Name1")
                .email("some1@email.ru")
                .build();
        userService.createUserDto(userDto1);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .requestId(1L)
                .build();
        itemService.createItemDto(itemRequestDto, userDto.getId());

        bookingRequestDto = BookingRequestDto.builder()
                .start(time1)
                .end(time2)
                .itemId(1L)
                .build();
    }

    @Test
    void testCreateBooking() {
        bookingService.createBooking(bookingRequestDto, 2L);
        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking b WHERE b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", 1L).getSingleResult();
        assertThat(booking.getStart(), equalTo(time1));
        assertThat(booking.getEnd(), equalTo(time2));
        assertThat(booking.getItem().getId(), equalTo(1L));
        assertThat(booking.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void testCreateBookingUserNotFound() {
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                bookingService.createBooking(bookingRequestDto, 10L));
        assertThat(exception.getMessage(), equalTo("User с id: 10 не найден"));
    }

    @Test
    void testCreateBookingOwnerError() {
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                bookingService.createBooking(bookingRequestDto, 1L));
        assertThat(exception.getMessage(), equalTo("Арендатор не может быть owner"));
    }

    @Test
    void testCreateBookingDataValidation() {
        bookingRequestDto.setStart(time2);
        bookingRequestDto.setEnd(time1);
        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                bookingService.createBooking(bookingRequestDto, 1L));
        assertThat(exception.getMessage(), equalTo("Start time не может быть позже End time"));
        bookingRequestDto.setStart(time1);
        bookingRequestDto.setEnd(time1);
        exception = assertThrows(BadRequestException.class, () ->
                bookingService.createBooking(bookingRequestDto, 1L));
        assertThat(exception.getMessage(), equalTo("Start time не может быть равным End time"));
    }

    @Test
    void testGetBookingById() {
        bookingService.createBooking(bookingRequestDto, 2L);
        BookingResponseDto booking = bookingService.getBookingById(2L, 1L);
        assertThat(booking.getItem().getId(), equalTo(1L));
    }

    @Test
    void testGetBookingByIdNotFound() {
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                bookingService.getBookingById(2L, 5L));
        assertThat(exception.getMessage(), equalTo("Booking с id: 5 не найден"));
    }

    @Test
    void testGetAllBookingsByStatusWaitingAndAll() {
        bookingService.createBooking(bookingRequestDto, 2L);
        List<BookingResponseDto> bookingList = bookingService.getAllBookingsByUserId(
                2L, "WAITING", 0, 5);
        assertThat(bookingList.size(), equalTo(1));
        assertThat(bookingList.get(0).getId(), equalTo(1L));
        List<BookingResponseDto> bookingList2 = bookingService.getAllBookingsByUserId(
                2L, "ALL", 0, 5);
        assertThat(bookingList2.size(), equalTo(1));
        assertThat(bookingList2.get(0).getId(), equalTo(1L));
    }

    @Test
    void testGetAllBookingsByStatusPast() {
        bookingService.createBooking(bookingRequestDto, 2L);
        List<BookingResponseDto> bookingList = bookingService.getAllBookingsByUserId(
                2L, "PAST", 6, 3);
        assertThat(bookingList.size(), equalTo(0));
    }

    @Test
    void testGetAllBookingsByStatusFutureAndCurrent() {
        bookingService.createBooking(bookingRequestDto, 2L);
        List<BookingResponseDto> bookingList = bookingService.getAllBookingsByUserId(
                2L, "FUTURE", 0, 5);
        assertThat(bookingList.size(), equalTo(1));
        assertThat(bookingList.get(0).getId(), equalTo(1L));
        List<BookingResponseDto> bookingList1 = bookingService.getAllBookingsByUserId(
                2L, "CURRENT", 4, 4);
        assertThat(bookingList1.size(), equalTo(0));
    }

    @Test
    void testGetAllBookingsByOwnerIdByStatus() {
        bookingService.createBooking(bookingRequestDto, 2L);
        List<BookingResponseDto> bookingList = bookingService.getAllBookingsByOwnerId(
                1L, "WAITING", 0, 5);
        assertThat(bookingList.size(), equalTo(1));
        assertThat(bookingList.get(0).getId(), equalTo(1L));
        List<BookingResponseDto> bookingList2 = bookingService.getAllBookingsByOwnerId(
                1L, "ALL", 0, 3);
        assertThat(bookingList2.size(), equalTo(1));
        assertThat(bookingList2.get(0).getId(), equalTo(1L));
        List<BookingResponseDto> bookingList3 = bookingService.getAllBookingsByOwnerId(
                1L, "PAST", 0, 1);
        assertThat(bookingList3.size(), equalTo(0));
        List<BookingResponseDto> bookingList4 = bookingService.getAllBookingsByOwnerId(
                1L, "FUTURE", 0, 5);
        assertThat(bookingList4.size(), equalTo(1));
        assertThat(bookingList4.get(0).getId(), equalTo(1L));
        List<BookingResponseDto> bookingList5 = bookingService.getAllBookingsByOwnerId(
                1L, "CURRENT", 0, 3);
        assertThat(bookingList5.size(), equalTo(0));
    }

    @Test
    void testGetAllByOwnerUserNOtFound() {
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                bookingService.getAllBookingsByOwnerId(3L, "CURRENT", 0, 2));
        assertThat(exception.getMessage(), equalTo("User с id: 3 не найден"));
    }
}
