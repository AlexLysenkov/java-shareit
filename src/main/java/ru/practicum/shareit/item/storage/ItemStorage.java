package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item createItem(Item item, Long userId);

    Item updateItem(Long id, Item item, Long userId);

    Item getItemById(Long itemId, Long userId);

    List<Item> getAllUserItems(Long userId);

    List<Item> searchItems(String text);
}
