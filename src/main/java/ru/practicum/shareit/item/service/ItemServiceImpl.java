package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public ItemDto createItemDto(ItemDto itemDto, Long userId) {
        checkUserExistsById(userId);
        User owner = userStorage.getUserById(userId);
        Item item = ItemMapper.dtoToItem(itemDto);
        log.info("Создан item пользователем с id: {}", userId);
        return ItemMapper.itemToDto(itemStorage.createItem(item, owner));
    }

    @Override
    public ItemDto updateItemDto(Long id, ItemDto itemDto, Long userId) {
        checkUserExistsById(userId);
        Item item = ItemMapper.dtoToItem(itemDto);
        log.info("Обновлен item с id: {} пользователем с id: {}", id, userId);
        return ItemMapper.itemToDto(itemStorage.updateItem(id, item, userId));
    }

    @Override
    public ItemDto getItemDtoById(Long itemId, Long userId) {
        checkUserExistsById(userId);
        log.info("Получен item с id: {} пользователем с id: {}", itemId, userId);
        return ItemMapper.itemToDto(itemStorage.getItemById(itemId, userId));
    }

    @Override
    public List<ItemDto> getAllUserItemsDto(Long userId) {
        checkUserExistsById(userId);
        log.info("Получены все items пользователя с id: {}", userId);
        return ItemMapper.listItemsToListDto(itemStorage.getAllUserItems(userId));
    }

    @Override
    public List<ItemDto> searchItemsDto(String text) {
        log.info("Найдены все items по запросу '{}'", text);
        return ItemMapper.listItemsToListDto(itemStorage.searchItems(text));
    }

    private void checkUserExistsById(Long id) {
        boolean userExistsById = userStorage.existsById(id);
        if (!userExistsById) {
            throw new ObjectNotFoundException(String.format("User с id: %d не найден", id));
        }
    }
}
