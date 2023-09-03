package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private static final String USER_NOT_FOUND = "User с id: %d не найден";
    private static final String ITEM_NOT_FOUND = "Item с id: %d не найден";

    @Override
    @Transactional
    public ItemResponseDto createItemDto(ItemRequestDto itemRequestDto, Long userId) {
        Item item = ItemMapper.dtoToItem(itemRequestDto);
        item.setOwner(userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format(USER_NOT_FOUND, userId))));
        log.info("Создан item пользователем с id: {}", userId);
        return ItemMapper.itemToDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemResponseDto updateItemDto(Long id, ItemRequestDto itemRequestDto, Long userId) {
        checkUserExistsById(userId);
        checkItemOwner(id, userId);
        Item item = itemRepository.findById(id).orElseThrow(() ->
                new ObjectNotFoundException(String.format(ITEM_NOT_FOUND, id)));
        Optional.ofNullable(itemRequestDto.getName()).ifPresent(item::setName);
        Optional.ofNullable(itemRequestDto.getDescription()).ifPresent(item::setDescription);
        Optional.ofNullable(itemRequestDto.getAvailable()).ifPresent(item::setAvailable);
        log.info("Обновлен item с id: {} пользователем с id: {}", id, userId);
        return ItemMapper.itemToDto(itemRepository.save(item));
    }

    @Override
    public ItemFullResponseDto getItemDtoById(Long itemId, Long userId) {
        checkUserExistsById(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ObjectNotFoundException(String.format(ITEM_NOT_FOUND, itemId)));
        if (item.getOwner().getId().equals(userId)) {
            Booking lastBooking = bookingRepository.findFirstByItemIdAndEndIsBeforeAndStatusIs(item.getId(),
                            LocalDateTime.now().plusHours(1), Status.APPROVED, Sort.by("end").descending())
                    .orElse(null);
            if (lastBooking != null && lastBooking.getStatus() == Status.REJECTED) {
                lastBooking = null;
            }
            Booking nextBooking = bookingRepository.findFirstByItemIdAndStartIsAfterAndStatusIs(item.getId(),
                            LocalDateTime.now(), Status.APPROVED, Sort.by("start").ascending())
                    .orElse(null);
            if (nextBooking != null && nextBooking.getStatus() == Status.REJECTED) {
                nextBooking = null;
            }
            return ItemMapper.toItemResponseDto(item, lastBooking, nextBooking,
                    commentRepository.findAllByItemId(itemId));
        }
        log.info("Получен item с id: {} пользователем с id: {}", itemId, userId);
        return ItemMapper.toItemResponseDto(item, null, null,
                commentRepository.findAllByItemId(itemId));
    }

    @Override
    public List<ItemFullResponseDto> getAllUserItemsDto(Long userId) {
        checkUserExistsById(userId);
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        List<Booking> lastBookings = bookingRepository.findFirstByItemIdInAndEndIsBeforeOrderByEndDesc(itemIds,
                LocalDateTime.now());
        List<Booking> nextBookings = bookingRepository.findFirstByItemIdInAndStartIsAfterOrderByStart(itemIds,
                LocalDateTime.now());
        Map<Long, Booking> lastBookingMap = lastBookings.stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity()));
        Map<Long, Booking> nextBookingMap = nextBookings.stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity()));
        Map<Long, List<Comment>> commentMap = commentRepository
                .findAllByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
        List<ItemFullResponseDto> itemFullResponseDtos = new ArrayList<>();
        for (Item item : items) {
            itemFullResponseDtos.add(ItemMapper.toItemResponseDto(item, lastBookingMap.get(item.getId()),
                    nextBookingMap.get(item.getId()), commentMap.getOrDefault(item.getId(), Collections.emptyList())));
        }
        return itemFullResponseDtos;
    }

    @Override
    public List<ItemResponseDto> searchItemsDto(String text, Long userId) {
        checkUserExistsById(userId);
        if (text.isBlank()) {
            log.info("По запросу User ID {}, получен пустой лист", userId);
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.getAvailableItemByText(text);
        log.info("Найдены все items по запросу {}", text);
        return ItemMapper.listItemsToListDto(items);
    }

    @Override
    @Transactional
    public CommentResponseDto createComment(Long userId, Long itemId, CommentRequestDto commentRequestDto) {
        if (bookingRepository.findBookersAndItems(userId, itemId).isEmpty()) {
            throw new BadRequestException("Оставлять Comment может только User, с завершенным Booking");
        }
        LocalDateTime time = LocalDateTime.now();
        User author = userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format(USER_NOT_FOUND, userId)));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ObjectNotFoundException(String.format(ITEM_NOT_FOUND, itemId)));
        Comment comment = CommentMapper.dtoToComment(commentRequestDto, author, item, time);
        log.info("Создан comment пользователем с id: {}, для item с id:{}", userId, itemId);
        return CommentMapper.toResponseDto(commentRepository.save(comment));
    }

    private void checkUserExistsById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ObjectNotFoundException(String.format(USER_NOT_FOUND, id));
        }
    }

    private void checkItemOwner(Long itemId, Long userId) {
        Item item = itemRepository.getReferenceById(itemId);
        if (item.getOwner() == null || !item.getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException(String.format("User с id: %d не является владельцем item", userId));
        }
    }
}
