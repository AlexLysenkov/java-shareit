package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemResponseDto createItemDto(ItemRequestDto itemRequestDto, Long userId);

    ItemResponseDto updateItemDto(Long id, ItemRequestDto itemRequestDto, Long userId);

    ItemFullResponseDto getItemDtoById(Long itemId, Long userId);

    List<ItemFullResponseDto> getAllUserItemsDto(Long userId, Integer from, Integer size);

    List<ItemResponseDto> searchItemsDto(String text, Long userId, Integer from, Integer size);

    CommentResponseDto createComment(Long userId, Long itemId, CommentRequestDto commentRequestDto);
}
