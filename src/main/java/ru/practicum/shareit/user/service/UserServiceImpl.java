package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUserDto(UserDto userDto) {
        User user = UserMapper.dtoToUser(userDto);
        try {
            log.info("User с id: {} создан", userDto.getId());
            return UserMapper.userToDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Пользователь с таким email уже существует");
        }
    }

    @Override
    public UserDto updateUserDto(Long id, UserDto userDto) {
        checkUserExistsById(id);
        try {
            User user = UserMapper.dtoToUser(getUserDtoById(id));
            if (userDto.getName() != null && !userDto.getName().isBlank()) {
                user.setName(userDto.getName());
            }
            if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
                user.setEmail(userDto.getEmail());
            }
            log.info("User с id: {} обновлен", id);
            return UserMapper.userToDto(userRepository.saveAndFlush(user));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Пользователь с таким email уже существует");
        }
    }

    @Override
    public UserDto getUserDtoById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ObjectNotFoundException(String.format("User с id: %d не найден", id)));
        log.info("User с id: {} получен", id);
        return UserMapper.userToDto(user);
    }

    @Override
    public void deleteUserDtoById(Long id) {
        checkUserExistsById(id);
        userRepository.deleteById(id);
        log.info("User с id: {} удален", id);
    }

    @Override
    public List<UserDto> getAllUsersDto() {
        log.info("Список всех Users получен");
        return UserMapper.listUsersToListDto(userRepository.findAll());
    }

    private void checkUserExistsById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ObjectNotFoundException(String.format("User с id: %d не найден", id));
        }
    }
}
