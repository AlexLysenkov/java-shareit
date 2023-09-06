package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceImplTest {
    private final EntityManager em;
    private final UserService userService;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @InjectMocks
    ItemServiceImpl itemServiceImpl;
    private final ItemService itemService;
    private CommentRequestDto commentRequestDto;
    private UserDto userDto;
    private ItemRequestDto itemDto;

    @BeforeEach
    void setUp() {
        commentRequestDto = CommentRequestDto.builder()
                .text("text")
                .build();

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
        List<ItemFullResponseDto> items = itemService.getAllUserItemsDto(1L, 0, 10);
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getName(), equalTo("Item"));
        assertThat(items.get(0).getDescription(), equalTo("Description"));
    }

    @Test
    void testSearchItem() {
        userService.createUserDto(userDto);
        itemService.createItemDto(itemDto, 1L);
        List<ItemResponseDto> items = itemService.searchItemsDto("IteM", 1L, 0, 10);
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getDescription(), equalTo("Description"));
    }

    @Test
    void testCreateCommentError() {
        userService.createUserDto(userDto);
        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                itemService.createComment(1L, 5L, commentRequestDto));
        assertThat(exception.getMessage(),
                equalTo("Оставлять Comment может только User, с завершенным Booking"));
    }

    @Test
    void testCreateComment() {
        User user = new User(1L, "Name", "some@email.ru");
        User user2 = new User(2L, "NewName", "name@email.ru");
        Item item = new Item(1L, "Item", "Description", true, user, 1L);
        Booking booking = new Booking(1L, LocalDateTime.of(2022, 1, 12, 5, 15),
                LocalDateTime.of(2022, 1, 22, 5, 15), item, user2, Status.APPROVED);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findBookersAndItems(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Collections.singletonList(booking));
        Mockito.when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CommentResponseDto commentResponseDto = itemServiceImpl.createComment(1L, 1L, commentRequestDto);
        assertNotNull(commentResponseDto);
        assertEquals(commentRequestDto.getText(), commentResponseDto.getText());
        assertEquals(user.getId(), 1L);
        assertEquals(item.getId(), 1L);
        Mockito.verify(userRepository).findById(1L);
        Mockito.verify(itemRepository).findById(1L);
        Mockito.verify(bookingRepository).findBookersAndItems(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(commentRepository).save(any(Comment.class));
    }
}
