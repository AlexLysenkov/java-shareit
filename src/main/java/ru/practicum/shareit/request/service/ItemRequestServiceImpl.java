package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDtoResponse createItemRequest(ItemRequestInfoDto itemRequestInfoDto, Long userId) {
        ItemRequest itemRequest = ItemRequestMapper.dtoToItemRequest(itemRequestInfoDto);
        itemRequest.setRequester(userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("User с id: %d не найден", userId))
        ));
        itemRequest.setCreated(LocalDateTime.now());
        log.info("Создан itemRequest пользователем с id: {}", userId);
        return ItemRequestMapper.itemRequestToDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDtoResponse> getItemRequestsByRequesterId(Long userId) {
        checkUserExistsById(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterId(userId,
                Sort.by("created").descending());
        return getItemRequests(itemRequests);
    }

    @Override
    public List<ItemRequestDtoResponse> getAllItemRequests(Integer from, Integer size, Long userId) {
        checkUserExistsById(userId);
        Pageable pageable = PageRequest.of(from, size, Sort.by("created").descending());
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdIsNot(userId, pageable);
        return getItemRequests(itemRequests);
    }

    @Override
    public ItemRequestDtoResponse getItemRequestById(Long requestId, Long userId) {
        checkUserExistsById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Request с id: %d не найден", requestId)));
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        return ItemRequestMapper.toItemRequestDtoResponse(itemRequest, items);
    }

    private List<ItemRequestDtoResponse> getItemRequests(List<ItemRequest> itemRequests) {
        List<Item> itemsList = itemRepository.findByRequestIdIn(itemRequests.stream().map(ItemRequest::getId)
                .collect(Collectors.toList()));
        Map<Long, List<Item>> itemsByRequestId = itemsList.stream()
                .collect(Collectors.groupingBy(Item::getRequestId));
        List<ItemRequestDtoResponse> itemRequestDtoResponses = new LinkedList<>();
        for (ItemRequest itemRequest : itemRequests) {
            List<Item> items = itemsByRequestId.get(itemRequest.getId()) != null
                    ? itemsByRequestId.get(itemRequest.getId())
                    : Collections.emptyList();
            List<ItemResponseDto> itemsDto = ItemMapper.listItemsToListDto(items);
            itemRequestDtoResponses.add(ItemRequestMapper.toItemRequestDto(itemRequest, itemsDto));
        }
        return itemRequestDtoResponses;
    }

    private void checkUserExistsById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ObjectNotFoundException(String.format("User с id: %d не найден", id));
        }
    }
}
