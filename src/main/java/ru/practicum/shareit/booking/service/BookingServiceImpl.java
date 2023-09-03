package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private static final String USER_NOT_FOUND = "User с id: %d не найден";
    private static final String BOOKING_NOT_FOUND = "Booking с id: %d не найден";

    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format(USER_NOT_FOUND, userId)));
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
    @Transactional
    public BookingResponseDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ObjectNotFoundException(String.format(BOOKING_NOT_FOUND, bookingId)));
        checkUserExistsById(userId);
        Item item = booking.getItem();
        if (checkUserIsOwner(userId, item)) {
            throw new ObjectNotFoundException(String.format("User с id: %d не владелец item: %s", userId, item));
        }
        if (booking.getStatus().equals(Status.APPROVED) || booking.getStatus().equals(Status.REJECTED)) {
            throw new BadRequestException("Не позволено изменять Status");
        }
        if (approved != null) {
            booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        }
        booking = bookingRepository.save(booking);
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ObjectNotFoundException(String.format(BOOKING_NOT_FOUND, bookingId)));
        checkUserExistsById(userId);
        Item item = booking.getItem();
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new ObjectNotFoundException(String.format("User с id: %d не владелец item: %s", userId, item));
        }
        log.info("Получены данные о бронировании с id: {}", bookingId);
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAllBookingsByUserId(Long userId, String status, Integer from, Integer size) {
        checkUserExistsById(userId);
        State state;
        List<Booking> bookings;
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        try {
            state = State.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format("Unknown state: %s", status));
        }
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findPastBookings(userId, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookings(userId, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureBookings(userId, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusOrderById(userId, Status.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusOrderById(userId, Status.REJECTED, pageable);
                break;
            default:
                throw new BadRequestException(String.format("Передан неизвестный статус: %s", status));
        }
        return bookings.stream().map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getAllBookingsByOwnerId(Long ownerId, String status, Integer from, Integer size) {
        checkUserExistsById(ownerId);
        State state;
        List<Booking> bookings;
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        try {
            state = State.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format("Unknown state: %s", status));
        }
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByOwnerId(ownerId, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findPastOwners(ownerId, LocalDateTime.now(), pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentOwners(ownerId, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureOwners(ownerId, LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findOwnersAndStatusEquals(ownerId, Status.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findOwnersAndStatusEquals(ownerId, Status.REJECTED, pageable);
                break;
            default:
                throw new BadRequestException(String.format("Передан неизвестный статус: %s", status));
        }
        return bookings.stream().map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
    }

    private void checkUserExistsById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ObjectNotFoundException(String.format(USER_NOT_FOUND, id));
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
