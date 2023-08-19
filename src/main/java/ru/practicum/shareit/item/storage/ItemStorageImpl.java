package ru.practicum.shareit.item.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 1L;
    private final UserService userService;

    @Autowired
    public ItemStorageImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Item createItem(Item item, Long userId) {
        userService.checkUserExistsById(userId);
        User owner = userService.getOwnerById(userId);
        item.setId(id++);
        item.setOwner(owner);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Long id, Item item, Long userId) {
        checkItem(id);
        userService.checkUserExistsById(userId);
        checkItemOwner(id, userId);
        Item oldItem = items.get(id);
        Optional.ofNullable(item.getName()).ifPresent(oldItem::setName);
        Optional.ofNullable(item.getDescription()).ifPresent(oldItem::setDescription);
        Optional.ofNullable(item.getAvailable()).ifPresent(oldItem::setAvailable);
        items.put(id, oldItem);
        return oldItem;
    }

    @Override
    public Item getItemById(Long itemId, Long userId) {
        checkItem(itemId);
        userService.checkUserExistsById(userId);
        return items.get(itemId);
    }

    @Override
    public List<Item> getAllUserItems(Long userId) {
        userService.checkUserExistsById(userId);
        return items.values()
                .stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return items.values()
                .stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    private void checkItem(Long id) {
        if (!items.containsKey(id)) {
            throw new ObjectNotFoundException(String.format("Item с id: %d не найден", id));
        }
    }

    private void checkItemOwner(Long itemId, Long userId) {
        if (items.get(itemId).getOwner() == null || !items.get(itemId).getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException(String.format("User с id: %d не является владельцем item", userId));
        }
    }
}
