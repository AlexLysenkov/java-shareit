package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import java.util.List;

@Controller
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("Получен POST запрос по эндпоинту '/users' на добавление user {}", userDto);
        return new ResponseEntity<>(userService.createUserDto(userDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Validated(Update.class) @RequestBody UserDto userDto) {
        log.info("Получен PATCH запрос по эндпоинту '/users/{}' на обновление user с id {}", id, id);
        return ResponseEntity.ok(userService.updateUserDto(id, userDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        log.info("Получен GET запрос по эндпоинту '/users/{}' на получение user по id {}", id, id);
        return ResponseEntity.ok(userService.getUserDtoById(id));
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
        return ResponseEntity.ok(userService.getAllUsersDto());
    }
}
