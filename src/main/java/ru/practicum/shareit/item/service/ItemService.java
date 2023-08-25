package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemDto createItemDto(ItemDto itemDto, Long userId);

    ItemDto updateItemDto(Long id, ItemDto itemDto, Long userId);

    ItemResponseDto getItemDtoById(Long itemId, Long userId);

    List<ItemResponseDto> getAllUserItemsDto(Long userId);

    List<ItemDto> searchItemsDto(String text, Long userId);

    CommentResponseDto createComment(Long userId, Long itemId, CommentRequestDto commentRequestDto);
}
