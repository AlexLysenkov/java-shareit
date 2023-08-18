package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto itemToDto(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item не может быть null");
        }
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .build();
    }

    public static Item dtoToItem(ItemDto itemDto) {
        if (itemDto == null) {
            throw new IllegalArgumentException("ItemDto не может быть null");
        }
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwner())
                .build();
    }

    public static List<ItemDto> listItemsToListDto(Collection<Item> items) {
        return items.stream().map(ItemMapper::itemToDto).collect(Collectors.toList());
    }
}