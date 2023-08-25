package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto createItemDto(ItemDto itemDto, Long userId) {
        Item item = ItemMapper.dtoToItem(itemDto);
        item.setOwner(userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("User с id: %d не найден", userId))));
        log.info("Создан item пользователем с id: {}", userId);
        return ItemMapper.itemToDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItemDto(Long id, ItemDto itemDto, Long userId) {
        checkUserExistsById(userId);
        checkItemOwner(id, userId);
        Item item = itemRepository.findById(id).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Item с id: %d не найден", id)));
        Optional.ofNullable(itemDto.getName()).ifPresent(item::setName);
        Optional.ofNullable(itemDto.getDescription()).ifPresent(item::setDescription);
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(item::setAvailable);
        log.info("Обновлен item с id: {} пользователем с id: {}", id, userId);
        return ItemMapper.itemToDto(itemRepository.save(item));
    }

    @Override
    public ItemResponseDto getItemDtoById(Long itemId, Long userId) {
        checkUserExistsById(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Item с id: %d не найден", itemId)));
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
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
            return ItemResponseDto.toItemResponseDto(item, lastBooking, nextBooking,
                    commentRepository.findAllByItemId(itemId));
        }
        log.info("Получен item с id: {} пользователем с id: {}", itemId, userId);
        return ItemResponseDto.toItemResponseDto(item, null, null,
                commentRepository.findAllByItemId(itemId));
    }

    @Override
    public List<ItemResponseDto> getAllUserItemsDto(Long userId) {
        checkUserExistsById(userId);
        List<ItemResponseDto> itemResponseDto = itemRepository.findAllByOwnerId(userId).stream()
                .map(item -> {
                    List<Comment> comments = commentRepository.findAllByItemId(item.getId());
                    Booking lastBooking = bookingRepository.findFirstByItemIdAndEndIsBeforeAndStatusIs(
                            item.getId(), LocalDateTime.now(), Status.APPROVED,
                            Sort.by("end").descending()).orElse(null);
                    Booking nextBooking = bookingRepository.findFirstByItemIdAndStartIsAfterAndStatusIs(
                            item.getId(), LocalDateTime.now(), Status.APPROVED,
                            Sort.by("start").ascending()).orElse(null);
                    return ItemResponseDto.toItemResponseDto(item, lastBooking, nextBooking, comments);
                }).collect(Collectors.toList());
        log.info("Получены все items пользователя с id: {}", userId);
        return itemResponseDto;
    }

    @Override
    public List<ItemDto> searchItemsDto(String text, Long userId) {
        checkUserExistsById(userId);
        if (text == null || text.isBlank()) {
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
        if (bookingRepository.findAllByBookerIdAndItemIdAndEndIsBefore(userId, itemId,
                LocalDateTime.now(), Sort.by("end").descending()).isEmpty()) {
            throw new BadRequestException("Оставлять Comment может только User, с завершенным Booking");
        }
        LocalDateTime time = LocalDateTime.now();
        User author = userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("User с id: %d не найден", userId)));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Item с id: %d не найден", itemId)));
        Comment comment = CommentMapper.dtoToComment(commentRequestDto, author, item, time);
        log.info("Создан comment пользователем с id: {}, для item с id:{}", userId, itemId);
        return CommentMapper.toResponseDto(commentRepository.save(comment));
    }

    private void checkUserExistsById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ObjectNotFoundException(String.format("User с id: %d не найден", id));
        }
    }

    private void checkItemOwner(Long itemId, Long userId) {
        Item item = itemRepository.getReferenceById(itemId);
        if (item.getOwner() == null || !item.getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException(String.format("User с id: %d не является владельцем item", userId));
        }
    }
}
