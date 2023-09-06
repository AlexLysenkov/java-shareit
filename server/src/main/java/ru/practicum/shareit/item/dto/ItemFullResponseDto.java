package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemFullResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingShortResponseDto lastBooking;
    private BookingShortResponseDto nextBooking;
    private Long requestId;
    private List<CommentResponseDto> comments;
}
