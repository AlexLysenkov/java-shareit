package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@Slf4j
@Validated
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestInfoDto> createItemRequest(@Valid @RequestBody ItemRequestInfoDto itemRequestInfoDto,
                                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен POST запрос по эндпоинту '/requests' от user с id {} на добавление ItemRequest {}",
                itemRequestInfoDto, userId);
        return new ResponseEntity<>(itemRequestService.createItemRequest(itemRequestInfoDto, userId),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDtoResponse>> getItemRequestsByRequesterId(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен GET запрос по эндпоинту '/requests' на получение ItemRequest для user с id {}", userId);
        return new ResponseEntity<>(itemRequestService.getItemRequestsByRequesterId(userId), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDtoResponse>> getAllItemRequests(@RequestParam(defaultValue = "0")
                                                                           @PositiveOrZero Integer from,
                                                                           @RequestParam(defaultValue = "10")
                                                                           @Positive Integer size,
                                                                           @RequestHeader("X-Sharer-User-Id")
                                                                           Long userId) {
        log.info("Получен GET запрос по эндпоинту '/requests/all' от user с id {} на получение всех ItemRequests",
                userId);
        return new ResponseEntity<>(itemRequestService.getAllItemRequests(from, size, userId), HttpStatus.OK);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDtoResponse> getItemRequestById(@PathVariable Long requestId,
                                                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен GET запрос по эндпоинту '/requests/{}'  от user с id {} на получение ItemRequest c id {}",
                requestId, userId, requestId);
        return new ResponseEntity<>(itemRequestService.getItemRequestById(requestId, userId), HttpStatus.OK);
    }
}
