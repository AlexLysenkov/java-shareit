package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

@Controller
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("Получен POST запрос по эндпоинту '/users' на добавление user {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id, @Validated(Update.class) @RequestBody UserDto userDto) {
        log.info("Получен PATCH запрос по эндпоинту '/users/{}' на обновление user с id {}", id, id);
        return userClient.updateUserDto(id, userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        log.info("Получен GET запрос по эндпоинту '/users/{}' на получение user по id {}", id, id);
        return userClient.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUserById(@PathVariable Long id) {
        log.info("Получен DELETE запрос по эндпоинту '/users/{}' на удаление user по id {}", id, id);
        return userClient.deleteUserById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Получен GET запрос по эндпоинту '/users' на получение всех users");
        return userClient.getAllUsers();
    }
}
