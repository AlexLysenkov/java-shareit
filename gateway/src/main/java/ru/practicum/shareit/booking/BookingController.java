package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.BadRequestException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constant.HEADER;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(HEADER) long userId,
                                                @RequestBody @Valid BookingRequestDto bookingRequestDto) {
        log.info("Получен POST запрос по эндпоинту '/bookings' от user с id {} на добавление bookings {}",
                userId, bookingRequestDto);
        return bookingClient.createBooking(bookingRequestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader(HEADER) Long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam("approved") Boolean approved) {
        log.info("Получен PATCH запрос по эндпоинту '/bookings/{}' от user c id {} статус подтверждения "
                        + "(approved: {}) booking с id {}",
                bookingId, userId, approved, bookingId);
        return bookingClient.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(HEADER) Long userId, @PathVariable Long bookingId) {
        log.info("Получен GET запрос по эндпоинту '/bookings/{}' от user c id {} на получение booking с id {}",
                bookingId, userId, bookingId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByUserId(
            @RequestHeader(HEADER) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new BadRequestException("Unknown state: " + stateParam));
        log.info("Получен GET запрос по эндпоинту '/bookings' от user c id {} " +
                "на получение списка всех booking этого user", userId);
        return bookingClient.getAllBookingsByUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsByOwnerId(
            @RequestHeader(HEADER) Long ownerId,
            @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new BadRequestException("Unknown state: " + stateParam));
        log.info("Получен GET запрос по эндпоинту '/bookings/owner' от user c id {} на получение списка booking всех "
                + "items для которых он owner", ownerId);
        return bookingClient.getAllBookingsByOwnerId(ownerId, state, from, size);
    }
}
