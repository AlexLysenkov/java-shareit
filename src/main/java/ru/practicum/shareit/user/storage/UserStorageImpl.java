package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    @Override
    public User createUser(User user) {
        checkEmail(user.getEmail());
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(Long id, User user) {
        if (users.containsKey(id)) {
            if (user.getName() != null && !user.getName().isBlank()) {
                users.get(id).setName(user.getName());
            }
            if (user.getEmail() != null && !user.getEmail().isBlank() &&
                    !user.getEmail().equals(users.get(id).getEmail())) {
                checkEmail(user.getEmail());
                users.get(id).setEmail(user.getEmail());
            }
            return users.get(id);
        } else {
            throw new ObjectNotFoundException(String.format("User с id: %s не найден", id));
        }
    }

    @Override
    public User getUserById(Long id) {
//        checkUser(id);
        return users.get(id);
    }

    @Override
    public void deleteUserById(Long id) {
//        checkUser(id);
        users.remove(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    private void checkEmail(String email) {
        Collection<User> user = users.values();
        if (user.stream().anyMatch(repUser -> repUser.getEmail().equals(email))) {
            throw new DuplicateEmailException(String.format("Пользователь уже существует с таким email: %s", email));
        }
    }

    @Override
    public boolean existsById(Long id) {
        return users.containsKey(id);
    }
}
