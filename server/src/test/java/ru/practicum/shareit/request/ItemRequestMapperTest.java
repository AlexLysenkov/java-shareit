package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ItemRequestMapperTest {
    private ItemRequestDtoResponse itemRequestInfoDto;
    private ItemRequestInfoDto itemRequestDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        itemRequestDto = ItemRequestInfoDto.builder()
                .description("text")
                .build();
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Description")
                .created(LocalDateTime.now())
                .requester(new User(1L, "Name", "some@email.ru"))
                .build();
        itemRequestInfoDto = ItemRequestDtoResponse.builder()
                .id(1L)
                .description("Description")
                .created(LocalDateTime.now())
                .build();
        itemRequestInfoDto = ItemRequestMapper.itemRequestToDto(itemRequest);
    }

    @Test
    void testItemRequestToDto() {
        assertNotNull(itemRequestInfoDto);
        assertEquals(itemRequestInfoDto.getId(), itemRequest.getId());
        assertEquals(itemRequestInfoDto.getDescription(), itemRequest.getDescription());
        assertEquals(itemRequestInfoDto.getCreated(), itemRequest.getCreated());
    }

    @Test
    void testDtoToItemRequest() {
        itemRequest = ItemRequestMapper.dtoToItemRequest(itemRequestDto);
        assertNotNull(itemRequest);
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
    }

    @Test
    void testToItemRequestDtoResponse() {
        List<Item> items = new ArrayList<>();
        ItemRequestDtoResponse itemRequestDtoResponse = ItemRequestDtoResponse.builder()
                .id(1L)
                .description("Description")
                .created(LocalDateTime.of(2023, 12, 25, 15, 10, 33))
                .items(null)
                .build();
        ItemRequestMapper.toItemRequestDtoResponse(itemRequest, items);
        assertEquals(1L, itemRequestDtoResponse.getId());
        assertEquals("Description", itemRequestDtoResponse.getDescription());
        assertEquals(LocalDateTime.of(2023, 12, 25, 15, 10, 33),
                itemRequestDtoResponse.getCreated());
        assertNull(itemRequestDtoResponse.getItems());
    }
}
