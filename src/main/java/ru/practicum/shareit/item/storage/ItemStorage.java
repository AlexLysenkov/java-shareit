package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    public Item createItem(Item item, Long userId);

    public Item updateItem(Long id, Item item, Long userId);

    public Item getItemById(Long itemId, Long userId);

    public List<Item> getAllUserItems(Long userId);

    public List<Item> searchItems(String text);
}
