package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceImplTest {
    private final EntityManager em;
    private final UserService userService;

    @Test
    void testCreateUser() {
        UserDto userDto = new UserDto(1L, "Name", "some@email.ru");
        userService.createUserDto(userDto);
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();
        assertThat(user.getId(), equalTo(1L));
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void testCreateEmailDuplicate() {
        UserDto userDto = new UserDto(1L, "Name", "some@email.ru");
        UserDto userDto1 = new UserDto(2L, "Petr", "some@email.ru");
        userService.createUserDto(userDto);
        DuplicateEmailException exception = assertThrows(DuplicateEmailException.class, () ->
                userService.createUserDto(userDto1));
        assertThat(exception.getMessage(), equalTo("Пользователь с таким email уже существует"));
    }

    @Test
    void testUpdateUser() {
        UserDto userDto = new UserDto(1L, "Petr", "petr@email.ru");
        userService.createUserDto(userDto);
        userDto.setName("NewName");
        userDto.setEmail("new@email.ru");
        userService.updateUserDto(1L, userDto);
        assertThat("NewName", equalTo(userDto.getName()));
        assertThat("new@email.ru", equalTo(userDto.getEmail()));
    }

    @Test
    void testUpdateUserError() {
        UserDto userDto = new UserDto(1L, "Alex", "alex@email.ru");
        userService.createUserDto(userDto);
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                userService.updateUserDto(10L, userDto));
        assertThat(exception.getMessage(), equalTo("User с id: 10 не найден"));
    }

    @Test
    void testGetUserById() {
        UserDto userDto = new UserDto(1L, "Name", "some1@email.ru");
        UserDto userDto2 = new UserDto(2L, "NewName", "new1@email.ru");
        userService.createUserDto(userDto);
        userService.createUserDto(userDto2);
        assertThat(userDto, equalTo(userService.getUserDtoById(1L)));
        assertThat(userDto2, equalTo(userService.getUserDtoById(2L)));
    }

    @Test
    void testGetUserByIdError() {
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                userService.getUserDtoById(10L));
        assertThat(exception.getMessage(), equalTo("User с id: 10 не найден"));
    }

    @Test
    void testGetAllUsers() {
        UserDto userDto = new UserDto(1L, "Petr", "petr@email.ru");
        UserDto userDto1 = new UserDto(2L, "Alex", "alex@email.ru");
        UserDto userDto2 = new UserDto(3L, "Name", "some@email.ru");
        UserDto userDto3 = new UserDto(4L, "NewName", "new1@email.ru");
        userService.createUserDto(userDto);
        userService.createUserDto(userDto1);
        userService.createUserDto(userDto2);
        userService.createUserDto(userDto3);
        List<UserDto> users = userService.getAllUsersDto();
        assertThat(4, equalTo(users.size()));
    }

    @Test
    void testDeleteUserById() {
        UserDto userDto = new UserDto(1L, "Petr", "petr@email.ru");
        UserDto userDto1 = new UserDto(2L, "Alex", "alex@email.ru");
        userService.createUserDto(userDto);
        userService.createUserDto(userDto1);
        userService.deleteUserDtoById(1L);
        assertThat(1, equalTo(userService.getAllUsersDto().size()));
    }

    @Test
    void testDeleteUserByIdError() {
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                userService.deleteUserDtoById(5L));
        assertThat(exception.getMessage(), equalTo("User с id: 5 не найден"));
    }
}
