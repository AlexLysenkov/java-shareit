package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.utils.Constants.HEADER;

@Controller
@RequestMapping(path = "/requests")
@Slf4j
@Validated
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDtoResponse> createItemRequest(@Valid @RequestBody ItemRequestInfoDto itemRequestInfoDto,
                                                                    @RequestHeader(HEADER) Long userId) {
        log.info("Получен POST запрос по эндпоинту '/requests' от user с id {} на добавление ItemRequest {}",
                itemRequestInfoDto, userId);
        return new ResponseEntity<>(itemRequestService.createItemRequest(itemRequestInfoDto, userId),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDtoResponse>> getItemRequestsByRequesterId(
            @RequestHeader(HEADER) Long userId) {
        log.info("Получен GET запрос по эндпоинту '/requests' на получение ItemRequest для user с id {}", userId);
        return ResponseEntity.ok(itemRequestService.getItemRequestsByRequesterId(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDtoResponse>> getAllItemRequests(@RequestParam(defaultValue = "0")
                                                                           @PositiveOrZero Integer from,
                                                                           @RequestParam(defaultValue = "10")
                                                                           @Positive Integer size,
                                                                           @RequestHeader(HEADER)
                                                                           Long userId) {
        log.info("Получен GET запрос по эндпоинту '/requests/all' от user с id {} на получение всех ItemRequests",
                userId);
        return ResponseEntity.ok(itemRequestService.getAllItemRequests(from, size, userId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDtoResponse> getItemRequestById(@PathVariable Long requestId,
                                                                     @RequestHeader(HEADER) Long userId) {
        log.info("Получен GET запрос по эндпоинту '/requests/{}'  от user с id {} на получение ItemRequest c id {}",
                requestId, userId, requestId);
        return ResponseEntity.ok(itemRequestService.getItemRequestById(requestId, userId));
    }
}
