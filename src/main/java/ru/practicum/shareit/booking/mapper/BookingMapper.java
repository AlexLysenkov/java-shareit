package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIdAndBooker;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class BookingMapper {
    public BookingDto bookingToDto(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking не может быть null");
        }
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .build();
    }

    public Booking requestToBooking(BookingRequestDto bookingRequestDto) {
        return Booking.builder()
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .build();
    }

    public Booking dtoToBooking(BookingDto bookingDto) {
        if (bookingDto == null) {
            throw new IllegalArgumentException("BookingDto не может быть null");
        }
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(bookingDto.getItem())
                .booker(bookingDto.getBooker())
                .status(bookingDto.getStatus())
                .build();
    }

    public BookingResponseDto toBookingResponseDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.itemToDto(booking.getItem()))
                .booker(UserMapper.userToDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public List<BookingDto> listBookingToListDto(Collection<Booking> bookings) {
        return bookings.stream().map(BookingMapper::bookingToDto).collect(Collectors.toList());
    }

    public BookingDtoIdAndBooker bookingToDtoId(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingDtoIdAndBooker.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
