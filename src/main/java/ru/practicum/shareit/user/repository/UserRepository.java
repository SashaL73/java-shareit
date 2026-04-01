package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> getUserById(Long id);

    List<User> findAll();

    User save(User user);

    User updateUser(User user);

    void deleteUser(Long id);

    Optional<User> findUserByEmail(String email, Long userId);
}