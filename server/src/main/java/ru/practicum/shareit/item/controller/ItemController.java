package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.utils.Constants.HEADER;

@Slf4j
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(@RequestBody ItemRequestDto itemRequestDto,
                                                      @RequestHeader(HEADER) Long userId) {
        log.info("Получен POST запрос по эндпоинту '/items' от user c id {} на добавление item {}", userId,
                itemRequestDto);
        return new ResponseEntity<>(itemService.createItemDto(itemRequestDto, userId), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemResponseDto> updateItem(@PathVariable Long id,
                                                      @RequestBody ItemRequestDto itemRequestDto,
                                                      @RequestHeader(HEADER) Long userId) {
        log.info("Получен PATCH запрос по эндпоинту '/items/{}' от user c id {} на обновление данных item с id {}",
                id, userId, id);
        return ResponseEntity.ok(itemService.updateItemDto(id, itemRequestDto, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemFullResponseDto> getItemById(@PathVariable Long id,
                                                           @RequestHeader(HEADER) Long userId) {
        log.info("Получен GET запрос по эндпоинту '/items/{}' от user c id {} на получение item с id {}", id,
                userId, id);
        return ResponseEntity.ok(itemService.getItemDtoById(id, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemFullResponseDto>> getAllUserItems(@RequestHeader(HEADER) Long userId,
                                                                     @RequestParam(defaultValue = "0") Integer from,
                                                                     @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен GET запрос по эндпоинту '/items' от user c id {} на получение всех items",
                userId);
        return ResponseEntity.ok(itemService.getAllUserItemsDto(userId, from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemResponseDto>> searchItems(@RequestParam("text") String text,
                                                             @RequestHeader(HEADER) Long userId,
                                                             @RequestParam(defaultValue = "0") Integer from,
                                                             @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен GET запрос по эндпоинту '/items/search' от user c id {} на получение списка item " +
                "по запросу {}", userId, text);
        return ResponseEntity.ok(itemService.searchItemsDto(text, userId, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponseDto> createComment(@RequestHeader(HEADER) Long userId,
                                                            @PathVariable("itemId") Long itemId,
                                                            @RequestBody CommentRequestDto commentRequestDto) {
        log.info("Получен POST запрос по эндпоинту '/items/{}/comment' от user c id {} на добавление comment {}",
                itemId, userId, commentRequestDto);
        return ResponseEntity.ok(itemService.createComment(userId, itemId, commentRequestDto));
    }
}
