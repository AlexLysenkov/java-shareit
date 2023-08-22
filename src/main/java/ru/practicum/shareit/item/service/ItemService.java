package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItemDto(ItemDto itemDto, Long userId);

    ItemDto updateItemDto(Long id, ItemDto itemDto, Long userId);

    ItemDto getItemDtoById(Long itemId, Long userId);

    List<ItemDto> getAllUserItemsDto(Long userId);

    List<ItemDto> searchItemsDto(String text);
}
