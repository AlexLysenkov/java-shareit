package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

import static ru.practicum.shareit.utils.Constants.HEADER;

@Slf4j
@Controller
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(@Validated(Create.class)
                                                      @RequestBody ItemRequestDto itemRequestDto,
                                                      @RequestHeader(HEADER) Long userId) {
        log.info("Получен POST запрос по эндпоинту '/items' от user c id {} на добавление item {}", userId,
                itemRequestDto);
        return new ResponseEntity<>(itemService.createItemDto(itemRequestDto, userId), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemResponseDto> updateItem(@PathVariable Long id,
                                                      @Validated(Update.class)
                                                      @RequestBody ItemRequestDto itemRequestDto,
                                                      @RequestHeader(HEADER) Long userId) {
        log.info("Получен PATCH запрос по эндпоинту '/items/{}' от user c id {} на обновление данных item с id {}",
                id, userId, id);
        return ResponseEntity.ok(itemService.updateItemDto(id, itemRequestDto, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemFullResponseDto> getItemById(@PathVariable @Positive Long id,
                                                           @RequestHeader(HEADER) Long userId) {
        log.info("Получен GET запрос по эндпоинту '/items/{}' от user c id {} на получение item с id {}", id,
                userId, id);
        return ResponseEntity.ok(itemService.getItemDtoById(id, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemFullResponseDto>> getAllUserItems(@RequestHeader(HEADER) Long userId) {
        log.info("Получен GET запрос по эндпоинту '/items' от user c id {} на получение всех items",
                userId);
        return ResponseEntity.ok(itemService.getAllUserItemsDto(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemResponseDto>> searchItems(@RequestParam("text") String text,
                                                             @RequestHeader(HEADER) Long userId) {
        log.info("Получен GET запрос по эндпоинту '/items/search' от user c id {} на получение списка item " +
                "по запросу {}", userId, text);
        return ResponseEntity.ok(itemService.searchItemsDto(text, userId));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponseDto> createComment(@RequestHeader(HEADER) @Positive Long userId,
                                                            @PathVariable("itemId") @Positive Long itemId,
                                                            @Valid @RequestBody CommentRequestDto commentRequestDto) {
        log.info("Получен POST запрос по эндпоинту '/items/{}/comment' от user c id {} на добавление comment {}",
                itemId, userId, commentRequestDto);
        return ResponseEntity.ok(itemService.createComment(userId, itemId, commentRequestDto));
    }
}
