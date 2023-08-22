package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User updateUser(Long id, User user);

    User getUserById(Long id);

    void deleteUserById(Long id);

    List<User> getAllUsers();

    boolean existsById(Long id);
}
