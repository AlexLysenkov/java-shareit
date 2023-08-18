package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto createUserDto(UserDto userDto) {
        User user = UserMapper.dtoToUser(userDto);
        log.info("User с id: {} создан", userDto.getId());
        return UserMapper.userToDto(userStorage.createUser(user));
    }

    @Override
    public UserDto updateUserDto(Long id, UserDto userDto) {
        User user = UserMapper.dtoToUser(userDto);
        log.info("User с id: {} обновлен", id);
        return UserMapper.userToDto(userStorage.updateUser(id, user));
    }

    @Override
    public UserDto getUserDtoById(Long id) {
        log.info("User с id: {} получен", id);
        return UserMapper.userToDto(userStorage.getUserById(id));
    }

    @Override
    public void deleteUserDtoById(Long id) {
        userStorage.deleteUserById(id);
        log.info("User с id: {} удален", id);
    }

    @Override
    public List<UserDto> getAllUsersDto() {
        log.info("Список всех Users получен");
        return UserMapper.listUsersToListDto(userStorage.getAllUsers());
    }
}
