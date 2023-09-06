package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constant.HEADER;

@Slf4j
@Controller
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Validated(Create.class)
                                             @RequestBody ItemRequestDto itemRequestDto,
                                             @RequestHeader(HEADER) Long userId) {
        log.info("Получен POST запрос по эндпоинту '/items' от user c id {} на добавление item {}", userId,
                itemRequestDto);
        return itemClient.createItem(itemRequestDto, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@PathVariable Long id,
                                             @Validated(Update.class)
                                             @RequestBody ItemRequestDto itemRequestDto,
                                             @RequestHeader(HEADER) Long userId) {
        log.info("Получен PATCH запрос по эндпоинту '/items/{}' от user c id {} на обновление данных item с id {}",
                id, userId, id);
        return itemClient.updateItem(id, itemRequestDto, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable @Positive Long id,
                                              @RequestHeader(HEADER) Long userId) {
        log.info("Получен GET запрос по эндпоинту '/items/{}' от user c id {} на получение item с id {}", id,
                userId, id);
        return itemClient.getItemById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(@RequestHeader(HEADER) Long userId,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                  @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен GET запрос по эндпоинту '/items' от user c id {} на получение всех items",
                userId);
        return itemClient.getAllUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam("text") String text,
                                              @RequestHeader(HEADER) Long userId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен GET запрос по эндпоинту '/items/search' от user c id {} на получение списка item " +
                "по запросу {}", userId, text);
        return itemClient.searchItems(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(HEADER) @Positive Long userId,
                                                @PathVariable("itemId") @Positive Long itemId,
                                                @Valid @RequestBody CommentRequestDto commentRequestDto) {
        log.info("Получен POST запрос по эндпоинту '/items/{}/comment' от user c id {} на добавление comment {}",
                itemId, userId, commentRequestDto);
        return itemClient.createComment(userId, itemId, commentRequestDto);
    }
}
