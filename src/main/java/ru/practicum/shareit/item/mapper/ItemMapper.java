package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemFullResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {
    public ItemResponseDto itemToDto(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item не может быть null");
        }
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .build();
    }

    public ItemShortResponseDto itemToShort(Item item) {
        return ItemShortResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    public Item dtoToItem(ItemRequestDto itemRequestDto) {
        if (itemRequestDto == null) {
            throw new IllegalArgumentException("ItemDto не может быть null");
        }
        return Item.builder()
                .id(itemRequestDto.getId())
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .available(itemRequestDto.getAvailable())
                .requestId(itemRequestDto.getRequestId())
                .build();
    }

    public List<ItemResponseDto> listItemsToListDto(Collection<Item> items) {
        return items.stream().map(ItemMapper::itemToDto).collect(Collectors.toList());
    }

    public ItemFullResponseDto toItemResponseDto(Item item, Booking lastBooking, Booking nextBooking,
                                                 List<Comment> comments) {
        return ItemFullResponseDto.builder()
                .nextBooking(BookingMapper.bookingToDtoId(nextBooking))
                .lastBooking(BookingMapper.bookingToDtoId(lastBooking))
                .name(item.getName())
                .id(item.getId())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .comments(CommentMapper.listCommentsToListResponse(comments))
                .build();
    }
}
