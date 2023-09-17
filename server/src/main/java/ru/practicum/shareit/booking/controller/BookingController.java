package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.utils.Constants.HEADER;

@Slf4j
@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(@RequestBody BookingRequestDto bookingRequestDto,
                                                            @RequestHeader(HEADER) Long userId) {
        log.info("Получен POST запрос по эндпоинту '/bookings' от user с id {} на добавление bookings {}",
                userId, bookingRequestDto);
        return new ResponseEntity<>(bookingService.createBooking(bookingRequestDto, userId), HttpStatus.CREATED);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> updateBooking(@RequestHeader(HEADER) Long userId,
                                                            @PathVariable Long bookingId,
                                                            @RequestParam("approved") Boolean approved) {
        log.info("Получен PATCH запрос по эндпоинту '/bookings/{}' от user c id {} статус подтверждения "
                        + "(approved: {}) booking с id {}",
                bookingId, userId, approved, bookingId);
        return ResponseEntity.ok(bookingService.updateBooking(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBookingById(@PathVariable Long bookingId,
                                                             @RequestHeader(HEADER) Long userId) {
        log.info("Получен GET запрос по эндпоинту '/bookings/{}' от user c id {} на получение booking с id {}",
                bookingId, userId, bookingId);
        return ResponseEntity.ok(bookingService.getBookingById(userId, bookingId));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAllBookingsByUserId(
            @RequestHeader(HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен GET запрос по эндпоинту '/bookings' от user c id {} " +
                "на получение списка всех booking этого user", userId);
        return ResponseEntity.ok(bookingService.getAllBookingsByUserId(userId, state, from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getAllBookingsByOwnerId(
            @RequestHeader(HEADER) Long ownerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен GET запрос по эндпоинту '/bookings/owner' от user c id {} на получение списка booking всех "
                + "items для которых он owner", ownerId);
        return ResponseEntity.ok(bookingService.getAllBookingsByOwnerId(ownerId, state, from, size));
    }
}
