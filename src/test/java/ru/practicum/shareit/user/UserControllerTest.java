package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    private User user;
    private UserDto userDto;
    private UserDto userDto1;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Name")
                .email("some@email.ru")
                .build();
        userDto = UserMapper.userToDto(user);
        User user1 = User.builder()
                .id(2L)
                .name("NewName")
                .email("new@email.ru")
                .build();
        userDto1 = UserMapper.userToDto(user1);
    }

    @Test
    void testCreateUser() throws Exception {
        Mockito
                .when(userService.createUserDto(Mockito.any())).thenReturn(userDto);
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
        Mockito.verify(userService).createUserDto(userDto);
    }

    @Test
    void testCreateUserStatus400() throws Exception {
        UserDto userDto2 = new UserDto(5L, "Person", "incorrectEmail$gmail.com");
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(userService);
    }

    @Test
    void testUpdateUser() throws Exception {
        userDto.setName("John");
        Mockito
                .when(userService.updateUserDto(Mockito.any(), Mockito.any())).thenReturn(userDto);
        mvc.perform(patch("/users/{id}", 1L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void testUpdateUserStatus400() throws Exception {
        userDto.setEmail("incorrectEmail$gmail.com");
        Mockito
                .when(userService.updateUserDto(Mockito.any(), Mockito.any())).thenReturn(userDto);
        mvc.perform(patch("/users/{id}", 1L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(userService);
    }

    @Test
    void testGetUserById() throws Exception {
        Mockito
                .when(userService.getUserDtoById(Mockito.any())).thenReturn(userDto);
        mvc.perform(get("/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
        Mockito.verify(userService).getUserDtoById(user.getId());
    }

    @Test
    void testGetAllUsers() throws Exception {
        Mockito
                .when(userService.getAllUsersDto()).thenReturn(List.of(userDto, userDto1));
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())))
                .andExpect(jsonPath("$[1].name", is(userDto1.getName())))
                .andExpect(jsonPath("$[1].email", is(userDto1.getEmail())));
        Mockito.verify(userService).getAllUsersDto();
    }

    @Test
    void testDeleteUser() throws Exception {
        mvc.perform(delete("/users/{id}", userDto.getId()))
                .andExpect(status().isOk());
        Mockito.verify(userService).deleteUserDtoById(1L);
    }
}
