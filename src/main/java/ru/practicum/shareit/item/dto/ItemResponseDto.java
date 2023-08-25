package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoIdAndBooker;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class ItemResponseDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private BookingDtoIdAndBooker lastBooking;
    private BookingDtoIdAndBooker nextBooking;
    private List<CommentResponseDto> comments;

    public static ItemResponseDto toItemResponseDto(Item item, Booking lastBooking, Booking nextBooking,
                                                    List<Comment> comments) {
        return ItemResponseDto.builder()
                .nextBooking(BookingMapper.bookingToDtoId(nextBooking))
                .lastBooking(BookingMapper.bookingToDtoId(lastBooking))
                .name(item.getName())
                .id(item.getId())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(CommentMapper.listCommentsToListResponse(comments))
                .build();
    }
}
