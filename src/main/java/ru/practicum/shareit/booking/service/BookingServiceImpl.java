package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("User с id: %d не найден", userId)));
        checkBookingDate(bookingRequestDto);
        Item item = itemRepository.findById(bookingRequestDto.getItemId()).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Item с id: %d не найден", bookingRequestDto.getItemId())));
        checkUserIsNotOwnerItem(item, userId);
        checkIsItemAvailable(item);
        Booking booking = BookingMapper.requestToBooking(bookingRequestDto);
        booking.setStatus(Status.WAITING);
        booking.setBooker(user);
        booking.setItem(item);
        bookingRepository.save(booking);
        log.info("User с id: {} создал Booking: {}", userId, booking);
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public BookingResponseDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Booking с id = %d не найден", bookingId)));
        checkUserExistsById(userId);
        Item item = booking.getItem();
        if (checkUserIsOwner(userId, item)) {
            throw new ObjectNotFoundException(String.format("User с id: %d не владелец item: %s", userId, item));
        }
        if (booking.getStatus().equals(Status.APPROVED) || booking.getStatus().equals(Status.REJECTED)) {
            throw new BadRequestException("Не позволено изменять Status");
        }
        if (approved != null) booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        booking = bookingRepository.save(booking);
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Booking с id = %d не найден", bookingId)));
        checkUserExistsById(userId);
        Item item = booking.getItem();
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new ObjectNotFoundException(String.format("User с id: %d не владелец item: %s", userId, item));
        }
        log.info("Получены данные о бронировании с id: {}", bookingId);
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAllBookingsByUserId(Long userId, String status) {
        checkUserExistsById(userId);
        State state;
        List<Booking> bookings = new ArrayList<>();
        try {
            state = State.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format("Unknown state: %s", status));
        }
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndIsBefore(userId, LocalDateTime.now(),
                        Sort.by("start").descending());
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), Sort.by("start").descending());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfter(userId, LocalDateTime.now(),
                        Sort.by("start").descending());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderById(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderById(userId, Status.REJECTED);
                break;
        }
        return bookings.isEmpty() ? Collections.emptyList() : bookings.stream()
                .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getAllBookingsByOwnerId(Long ownerId, String status) {
        checkUserExistsById(ownerId);
        State state;
        List<Booking> bookings = new ArrayList<>();
        try {
            state = State.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format("Unknown state: %s", status));
        }
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(ownerId, LocalDateTime.now(),
                        Sort.by("start").descending());
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(ownerId,
                        LocalDateTime.now(), LocalDateTime.now(), Sort.by("start").descending());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(ownerId, LocalDateTime.now(),
                        Sort.by("start").descending());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusEquals(ownerId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusEquals(ownerId, Status.REJECTED);
                break;
        }
        return bookings.isEmpty() ? Collections.emptyList() : bookings.stream()
                .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
    }

    private void checkUserExistsById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ObjectNotFoundException(String.format("User с id: %d не найден", id));
        }
    }

    private void checkBookingDate(BookingRequestDto bookingRequestDto) {
        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            throw new BadRequestException("Start time не может быть позже End time");
        }
        if (bookingRequestDto.getStart().equals(bookingRequestDto.getEnd())) {
            throw new BadRequestException("Start time не может быть равным End time");
        }
        if (bookingRequestDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Некорректный Start time");
        }
    }

    private void checkUserIsNotOwnerItem(Item item, Long userId) {
        if (item.getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("Арендатор не может быть owner");
        }
    }

    private void checkIsItemAvailable(Item item) {
        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь недоступна для бронирования");
        }
    }

    private boolean checkUserIsOwner(Long userId, Item item) {
        return !userId.equals(item.getOwner().getId());
    }
}
