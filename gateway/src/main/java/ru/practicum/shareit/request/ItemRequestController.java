package ru.practicum.shareit.request;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constant.HEADER;

@Controller
@RequestMapping(path = "/requests")
@Slf4j
@Validated
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody ItemRequestInfoDto itemRequestInfoDto,
                                                    @RequestHeader(HEADER) Long userId) {
        log.info("Получен POST запрос по эндпоинту '/requests' от user с id {} на добавление ItemRequest {}",
                itemRequestInfoDto, userId);
        return itemRequestClient.createItemRequest(itemRequestInfoDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByRequesterId(
            @RequestHeader(HEADER) Long userId) {
        log.info("Получен GET запрос по эндпоинту '/requests' на получение ItemRequest для user с id {}", userId);
        return itemRequestClient.getItemRequestsByRequesterId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestParam(defaultValue = "0")
                                                     @PositiveOrZero Integer from,
                                                     @RequestParam(defaultValue = "10")
                                                     @Positive Integer size,
                                                     @RequestHeader(HEADER)
                                                     Long userId) {
        log.info("Получен GET запрос по эндпоинту '/requests/all' от user с id {} на получение всех ItemRequests",
                userId);
        return itemRequestClient.getAllItemRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable Long requestId,
                                                     @RequestHeader(HEADER) Long userId) {
        log.info("Получен GET запрос по эндпоинту '/requests/{}'  от user с id {} на получение ItemRequest c id {}",
                requestId, userId, requestId);
        return itemRequestClient.getItemRequestById(requestId, userId);
    }
}
