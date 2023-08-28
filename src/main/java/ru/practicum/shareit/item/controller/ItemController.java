package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemRequestDto> createItem(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен POST запрос по эндпоинту '/items' от user c id {} на добавление item {}", userId,
                itemRequestDto);
        return new ResponseEntity<>(itemService.createItemDto(itemRequestDto, userId), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemRequestDto> updateItem(@PathVariable Long id, @RequestBody ItemRequestDto itemRequestDto,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен PATCH запрос по эндпоинту '/items/{}' от user c id {} на обновление данных item с id {}",
                id, userId, id);
        return new ResponseEntity<>(itemService.updateItemDto(id, itemRequestDto, userId), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDto> getItemById(@PathVariable @Positive Long id,
                                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен GET запрос по эндпоинту '/items/{}' от user c id {} на получение item с id {}", id,
                userId, id);
        return new ResponseEntity<>(itemService.getItemDtoById(id, userId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен GET запрос по эндпоинту '/items' от user c id {} на получение всех items",
                userId);
        return new ResponseEntity<>(itemService.getAllUserItemsDto(userId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemRequestDto>> searchItems(@RequestParam("text") String text,
                                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен GET запрос по эндпоинту '/items/search' от user c id {} на получение списка item " +
                "по запросу {}", userId, text);
        return new ResponseEntity<>(itemService.searchItemsDto(text, userId), HttpStatus.OK);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponseDto> createComment(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                            @PathVariable("itemId") @Positive Long itemId,
                                                            @Valid @RequestBody CommentRequestDto commentRequestDto) {
        log.info("Получен POST запрос по эндпоинту '/items/{}/comment' от user c id {} на добавление comment {}",
                itemId, userId, commentRequestDto);
        return new ResponseEntity<>(itemService.createComment(userId, itemId, commentRequestDto), HttpStatus.OK);
    }
}
