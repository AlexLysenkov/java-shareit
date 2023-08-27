package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
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
    public ItemRequestDto itemToDto(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item не может быть null");
        }
        return ItemRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
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
                .build();
    }

    public List<ItemRequestDto> listItemsToListDto(Collection<Item> items) {
        return items.stream().map(ItemMapper::itemToDto).collect(Collectors.toList());
    }

    public ItemResponseDto toResponseItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item не может быть null");
        }
        return ItemResponseDto.builder()
                .id(item.getId())
                .available(item.getAvailable())
                .description(item.getDescription())
                .name(item.getName())
                .build();
    }

    public ItemResponseDto toItemResponseDto(Item item, Booking lastBooking, Booking nextBooking,
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
