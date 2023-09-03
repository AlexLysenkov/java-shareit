package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;


@UtilityClass
public class ItemRequestMapper {
    public ItemRequestDtoResponse itemRequestToDto(ItemRequest itemRequest) {
        if (itemRequest == null) {
            throw new IllegalArgumentException("ItemRequest не может быть null.");
        }
        return ItemRequestDtoResponse.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public ItemRequest dtoToItemRequest(ItemRequestInfoDto itemRequestInfoDto) {
        if (itemRequestInfoDto == null) {
            throw new IllegalArgumentException("ItemRequestDto не может быть null.");
        }
        return ItemRequest.builder()
                .description(itemRequestInfoDto.getDescription())
                .build();
    }

    public ItemRequestDtoResponse toItemRequestDtoResponse(ItemRequest itemRequest, List<Item> items) {
        if (itemRequest == null) {
            throw new IllegalArgumentException("ItemRequest не может быть null.");
        }
        return ItemRequestDtoResponse.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(ItemMapper.listItemsToListDto(items))
                .build();
    }

    public ItemRequestDtoResponse toItemRequestDto(ItemRequest itemRequest, List<ItemResponseDto> items) {
        if (itemRequest == null) {
            throw new IllegalArgumentException("ItemRequest не может быть null.");
        }
        return ItemRequestDtoResponse.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }
}
