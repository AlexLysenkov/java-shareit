package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestServiceImplTest {
    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private ItemRequestInfoDto itemRequestInfoDto;
    private final UserService userService;

    @BeforeEach
    void setUp() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Name")
                .email("some@email.ru")
                .build();
        userService.createUserDto(userDto);

        itemRequestInfoDto = ItemRequestInfoDto.builder()
                .id(1L)
                .description("Description")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateItemRequest() {
        itemRequestService.createItemRequest(itemRequestInfoDto, 1L);
        TypedQuery<ItemRequest> query = em.createQuery(
                "SELECT i FROM ItemRequest i WHERE i.id = :id", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("id", itemRequestInfoDto.getId()).getSingleResult();
        assertThat(itemRequest.getId(), equalTo(1L));
        assertThat(itemRequest.getDescription(), equalTo(itemRequestInfoDto.getDescription()));
    }

    @Test
    void testCreateItemRequestUserNotFound() {
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                itemRequestService.createItemRequest(itemRequestInfoDto, 5L));
        assertThat(exception.getMessage(), equalTo("User с id: 5 не найден"));
    }

    @Test
    void testGetItemRequestById() {
        itemRequestService.createItemRequest(itemRequestInfoDto, 1L);
        assertThat(itemRequestService.getItemRequestById(1L, 1L).getId(), equalTo(1L));
        assertThat(itemRequestService.getItemRequestById(1L, 1L).getDescription(),
                equalTo("Description"));
    }

    @Test
    void testGetItemRequestNotFound() {
        itemRequestService.createItemRequest(itemRequestInfoDto, 1L);
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                itemRequestService.getItemRequestById(5L, 1L));
        assertThat(exception.getMessage(), equalTo("Request с id: 5 не найден"));
    }

    @Test
    void testGetAllItemRequests() {
        UserDto userDto = new UserDto(2L, "User", "user@email.ru");
        ItemRequestInfoDto itemRequestInfoDto1 = new ItemRequestInfoDto(2L, "text", LocalDateTime.now());
        userService.createUserDto(userDto);
        itemRequestService.createItemRequest(itemRequestInfoDto1, 2L);
        List<ItemRequestDtoResponse> itemRequests = itemRequestService.getAllItemRequests(0, 2, 1L);
        assertThat(itemRequests.size(), equalTo(1));
        assertThat(itemRequests.get(0).getId(), equalTo(1L));
    }

    @Test
    void testGetItemRequestsById() {
        itemRequestService.createItemRequest(itemRequestInfoDto, 1L);
        List<ItemRequestDtoResponse> itemRequests = itemRequestService.getItemRequestsByRequesterId(1L);
        assertThat(itemRequests.size(), equalTo(1));
        assertThat(itemRequests.get(0).getId(), equalTo(1L));
    }
}
