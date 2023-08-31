package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemFullResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceImplTest {
    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private UserDto userDto;
    private ItemRequestDto itemDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .name("Name")
                .email("some@email.ru")
                .build();
        itemDto = ItemRequestDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .requestId(1L)
                .build();
    }

    @Test
    void testCreateItem() {
        userService.createUserDto(userDto);
        itemService.createItemDto(itemDto, 1L);
        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto.getName()).getSingleResult();
        assertThat(item.getId(), equalTo(1L));
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void testCreateItemUserNotFound() {
        userService.createUserDto(userDto);
        itemService.createItemDto(itemDto, 1L);
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                itemService.createItemDto(itemDto, 5L));
        assertThat(exception.getMessage(), equalTo("User с id: 5 не найден"));
    }

    @Test
    void testUpdateItem() {
        userService.createUserDto(userDto);
        itemService.createItemDto(itemDto, 1L);
        itemDto.setName("NewName");
        itemDto.setDescription("NewDescription");
        itemService.updateItemDto(1L, itemDto, 1L);
        assertThat("NewName", equalTo(itemDto.getName()));
        assertThat("NewDescription", equalTo(itemDto.getDescription()));
    }

    @Test
    void testUpdateItemUserNotFound() {
        userService.createUserDto(userDto);
        itemService.createItemDto(itemDto, 1L);
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                itemService.updateItemDto(1L, itemDto, 3L));
        assertThat(exception.getMessage(), equalTo("User с id: 3 не найден"));
    }

    @Test
    void testGetItemById() {
        userService.createUserDto(userDto);
        itemService.createItemDto(itemDto, 1L);
        ItemFullResponseDto item = itemService.getItemDtoById(1L, 1L);
        assertThat(item.getName(), equalTo("Item"));
        assertThat(item.getDescription(), equalTo("Description"));
    }

    @Test
    void testGetItemByIdNotFound() {
        userService.createUserDto(userDto);
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                itemService.getItemDtoById(5L, 1L));
        assertThat(exception.getMessage(), equalTo("Item с id: 5 не найден"));
    }

    @Test
    void testGetAllItems() {
        userService.createUserDto(userDto);
        itemService.createItemDto(itemDto, userDto.getId());
        List<ItemFullResponseDto> items = itemService.getAllUserItemsDto(1L);
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getName(), equalTo("Item"));
        assertThat(items.get(0).getDescription(), equalTo("Description"));
    }

    @Test
    void testSearchItem() {
        userService.createUserDto(userDto);
        itemService.createItemDto(itemDto, 1L);
        List<ItemResponseDto> items = itemService.searchItemsDto("IteM", 1L);
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getDescription(), equalTo("Description"));
    }
}
