package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoResponse createItemRequest(ItemRequestInfoDto itemRequestInfoDto, Long userId);

    List<ItemRequestDtoResponse> getItemRequestsByRequesterId(Long userId);

    List<ItemRequestDtoResponse> getAllItemRequests(Integer from, Integer size, Long userId);

    ItemRequestDtoResponse getItemRequestById(Long requestId, Long userId);
}
