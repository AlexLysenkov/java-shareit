package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Получен POST запрос по эндпоинту '/users' на добавление user {}", userDto);
        return new ResponseEntity<>(userService.createUserDto(userDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("Получен PATCH запрос по эндпоинту '/users/{}' на обновление user с id {}", id, id);
        return new ResponseEntity<>(userService.updateUserDto(id, userDto), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        log.info("Получен GET запрос по эндпоинту '/users/{}' на получение user по id {}", id, id);
        return new ResponseEntity<>(userService.getUserDtoById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        log.info("Получен DELETE запрос по эндпоинту '/users/{}' на удаление user по id {}", id, id);
        userService.deleteUserDtoById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("Получен GET запрос по эндпоинту '/users' на получение всех users");
        return new ResponseEntity<>(userService.getAllUsersDto(), HttpStatus.OK);
    }
}
