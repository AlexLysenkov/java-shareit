package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemRequestDto createItemDto(ItemRequestDto itemRequestDto, Long userId);

    ItemRequestDto updateItemDto(Long id, ItemRequestDto itemRequestDto, Long userId);

    ItemResponseDto getItemDtoById(Long itemId, Long userId);

    List<ItemResponseDto> getAllUserItemsDto(Long userId);

    List<ItemRequestDto> searchItemsDto(String text, Long userId);

    CommentResponseDto createComment(Long userId, Long itemId, CommentRequestDto commentRequestDto);
}
