package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemStorage {
    Item createItem(Item item, User owner);

    Item updateItem(Long id, Item item, Long userId);

    Item getItemById(Long itemId, Long userId);

    List<Item> getAllUserItems(Long userId);

    List<Item> searchItems(String text);
}
