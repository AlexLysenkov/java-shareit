package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@Valid @RequestBody ItemDto itemDto,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен POST запрос по эндпоинту '/items' от user c id {} на добавление item {}", userId,
                itemDto);
        return new ResponseEntity<>(itemService.createItemDto(itemDto, userId), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long id, @RequestBody ItemDto itemDto,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен PATCH запрос по эндпоинту '/items/{}' от user c id {} на обновление данных item с id {}",
                id, userId, id);
        return new ResponseEntity<>(itemService.updateItemDto(id, itemDto, userId), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long id,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен GET запрос по эндпоинту '/items/{}' от user c id {} на получение item с id {}", id,
                userId, id);
        return new ResponseEntity<>(itemService.getItemDtoById(id, userId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен GET запрос по эндпоинту '/items' от user c id {} на получение всех items",
                userId);
        return new ResponseEntity<>(itemService.getAllUserItemsDto(userId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam("text") String text) {
        log.info("Получен GET запрос по эндпоинту '/items/search' на получение списка item по запросу '{}'",
                text);
        return new ResponseEntity<>(itemService.searchItemsDto(text), HttpStatus.OK);
    }
}
