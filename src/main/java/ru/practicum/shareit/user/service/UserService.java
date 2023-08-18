package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    public UserDto createUserDto(UserDto userDto);

    public UserDto updateUserDto(Long id, UserDto userDto);

    public UserDto getUserDtoById(Long id);

    public void deleteUserDtoById(Long id);

    public List<UserDto> getAllUsersDto();
}
