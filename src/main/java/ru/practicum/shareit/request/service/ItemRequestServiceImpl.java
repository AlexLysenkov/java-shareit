package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    public ItemRequestInfoDto createItemRequest(ItemRequestInfoDto itemRequestInfoDto, Long userId) {
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
        if (itemRequests.isEmpty()) {
            log.info("У User с id {} нет запросов", userId);
            return Collections.emptyList();
        }
        List<List<Item>> items = itemRequests.stream()
                .map(itemRequest -> itemRepository.findAllByRequestId(itemRequest.getId()))
                .collect(Collectors.toList());
        List<ItemRequestDtoResponse> itemRequestDtoResponses = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            for (List<Item> items1 : items) {
                itemRequestDtoResponses.add(ItemRequestMapper.toItemRequestDtoResponse(itemRequest, items1));
                break;
            }
            break;
        }
        log.info("Получен список ItemRequest для User c id {}", userId);
        return itemRequestDtoResponses;
    }

    @Override
    public List<ItemRequestDtoResponse> getAllItemRequests(Integer from, Integer size, Long userId) {
        checkUserExistsById(userId);
        Pageable pageable = PageRequest.of(from, size, Sort.by("created").descending());
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdIsNot(userId, pageable);
        List<ItemRequestDtoResponse> itemRequestDtoResponses = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            List<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());
            itemRequestDtoResponses.add(ItemRequestMapper.toItemRequestDtoResponse(itemRequest, items));
        }
        log.info("Получен полный список ItemRequest для User c id {}", userId);
        return itemRequestDtoResponses;
    }

    @Override
    public ItemRequestDtoResponse getItemRequestById(Long requestId, Long userId) {
        checkUserExistsById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Request с id: %d не найден", requestId)));
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        return ItemRequestMapper.toItemRequestDtoResponse(itemRequest, items);
    }

    private void checkUserExistsById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ObjectNotFoundException(String.format("User с id: %d не найден", id));
        }
    }
}
