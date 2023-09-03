package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemFullResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ItemMapperTest {
    private Item item;
    private ItemResponseDto itemResponseDto;
    private ItemShortResponseDto itemShortResponseDto;
    private ItemRequestDto itemRequestDto;
    private Booking last;
    private Booking next;

    @BeforeEach
    void setUp() {
        last = Booking.builder()
                .id(4L)
                .booker(User.builder().build())
                .build();
        next = Booking.builder()
                .id(5L)
                .booker(User.builder().build())
                .build();
        itemRequestDto = ItemRequestDto.builder()
                .id(2L)
                .name("Name1")
                .description("Description")
                .available(true)
                .build();
        item = Item.builder()
                .id(1L)
                .name("Name")
                .description("New Description")
                .available(true)
                .owner(User.builder().build())
                .build();
        itemResponseDto = ItemMapper.itemToDto(item);
        itemShortResponseDto = ItemMapper.itemToShort(item);
    }

    @Test
    void testItemToDto() {
        assertNotNull(item);
        assertEquals(item.getId(), itemResponseDto.getId());
        assertEquals(item.getName(), itemResponseDto.getName());
        assertEquals(item.getDescription(), itemResponseDto.getDescription());
        assertEquals(item.getAvailable(), itemResponseDto.getAvailable());
        assertEquals(item.getRequestId(), itemResponseDto.getRequestId());
    }

    @Test
    void testItemToShort() {
        assertNotNull(item);
        assertEquals(item.getId(), itemShortResponseDto.getId());
        assertEquals(item.getName(), itemShortResponseDto.getName());
    }

    @Test
    void testDtoToItem() {
        item = ItemMapper.dtoToItem(itemRequestDto);
        assertNotNull(itemRequestDto);
        assertEquals(itemRequestDto.getId(), item.getId());
        assertEquals(itemRequestDto.getName(), item.getName());
        assertEquals(itemRequestDto.getDescription(), item.getDescription());
        assertEquals(itemRequestDto.getAvailable(), item.getAvailable());
        assertEquals(itemRequestDto.getRequestId(), item.getRequestId());
    }

    @Test
    void testToItemResponseDtoList() {
        List<Item> items = new ArrayList<>();
        assertTrue(ItemMapper.listItemsToListDto(items).isEmpty());
        items.add(item);
        List<ItemResponseDto> actualItems = ItemMapper.listItemsToListDto(items);
        assertEquals(1, actualItems.size());
        assertEquals(1L, actualItems.get(0).getId());
        assertEquals("Name", actualItems.get(0).getName());
        assertEquals("New Description", actualItems.get(0).getDescription());
        assertEquals(true, actualItems.get(0).getAvailable());
    }

    @Test
    void testToItemResponseDto() {
        List<Comment> comments = new ArrayList<>();
        ItemFullResponseDto itemFullResponseDto = ItemFullResponseDto.builder()
                .id(1L)
                .name("Name")
                .description("Description")
                .available(true)
                .requestId(3L)
                .lastBooking(BookingMapper.bookingToDtoId(last))
                .nextBooking(BookingMapper.bookingToDtoId(next))
                .comments(null)
                .build();
        ItemMapper.toItemResponseDto(item, last, next, comments);
        assertEquals(1L, itemFullResponseDto.getId());
        assertEquals("Name", itemFullResponseDto.getName());
        assertEquals("Description", itemFullResponseDto.getDescription());
        assertEquals(true, itemFullResponseDto.getAvailable());
        assertEquals(3L, itemFullResponseDto.getRequestId());
        assertEquals(BookingMapper.bookingToDtoId(last), itemFullResponseDto.getLastBooking());
        assertEquals(BookingMapper.bookingToDtoId(next), itemFullResponseDto.getNextBooking());
        assertNull(itemFullResponseDto.getComments());
    }
}
