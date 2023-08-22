package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUserDto(UserDto userDto);

    UserDto updateUserDto(Long id, UserDto userDto);

    UserDto getUserDtoById(Long id);

    void deleteUserDtoById(Long id);

    List<UserDto> getAllUsersDto();
}
