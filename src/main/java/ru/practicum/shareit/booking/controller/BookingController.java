package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(@Valid @RequestBody BookingRequestDto bookingRequestDto,
                                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен POST запрос по эндпоинту '/bookings' от user с id {} на добавление bookings {}",
                userId, bookingRequestDto);
        return new ResponseEntity<>(bookingService.createBooking(bookingRequestDto, userId), HttpStatus.CREATED);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @PathVariable Long bookingId,
                                                            @RequestParam("approved") Boolean approved) {
        log.info("Получен PATCH запрос по эндпоинту '/bookings/{}' от user c id {} статус подтверждения "
                        + "(approved: {}) booking с id {}",
                bookingId, userId, approved, bookingId);
        return new ResponseEntity<>(bookingService.updateBooking(userId, bookingId, approved), HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBookingById(@PathVariable Long bookingId,
                                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен GET запрос по эндпоинту '/bookings/{}' от user c id {} на получение booking с id {}",
                bookingId, userId, bookingId);
        return new ResponseEntity<>(bookingService.getBookingById(userId, bookingId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAllBookingsByUserId(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен GET запрос по эндпоинту '/bookings' от user c id {} " +
                "на получение списка всех booking этого user", userId);
        return new ResponseEntity<>(bookingService.getAllBookingsByUserId(userId, state, from, size), HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getAllBookingsByOwnerId(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен GET запрос по эндпоинту '/bookings/owner' от user c id {} на получение списка booking всех "
                + "items для которых он owner", ownerId);
        return new ResponseEntity<>(bookingService.getAllBookingsByOwnerId(ownerId, state, from, size), HttpStatus.OK);
    }
}
