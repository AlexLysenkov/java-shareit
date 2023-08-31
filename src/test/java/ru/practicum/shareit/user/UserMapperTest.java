package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserMapperTest {
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Name")
                .email("some@email.ru")
                .build();
        userDto = UserMapper.userToDto(user);
    }

    @Test
    void testUserToDto() {
        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void testDtoToUser() {
        assertNotNull(user);
        assertEquals(UserMapper.userToDto(user).getId(), userDto.getId());
        assertEquals(UserMapper.userToDto(user).getName(), userDto.getName());
        assertEquals(UserMapper.userToDto(user).getEmail(), userDto.getEmail());
    }

    @Test
    void testToUserResponseDtoList() {
        List<User> userList = new ArrayList<>();
        assertTrue(UserMapper.listUsersToListDto(userList).isEmpty());
        userList.add(user);
        List<UserDto> actualUserList = UserMapper.listUsersToListDto(userList);
        assertEquals(1, actualUserList.size());
        assertEquals("Name", actualUserList.get(0).getName());
        assertEquals("some@email.ru", actualUserList.get(0).getEmail());
        assertEquals(1L, actualUserList.get(0).getId());
    }
}
