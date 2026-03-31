package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.*;

@Repository
public class MemoryUserRepository implements UserRepository {
    private final Map<Long, User> usersMap = new HashMap<>();
    private final Map<String, User> usersByEmail = new HashMap<>();
    private Long newId = 1L;

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(usersMap.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(usersMap.values());
    }

    @Override
    public User save(User user) {
        user.setId(newId++);
        usersMap.put(user.getId(), user);
        usersByEmail.put(user.getEmail(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        User oldUser = usersMap.get(user.getId());
        if (!oldUser.getEmail().equals(user.getEmail())) {
            usersByEmail.remove(oldUser.getEmail());
            usersByEmail.put(user.getEmail(), user);
        }
        usersMap.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        User removeUser = usersMap.get(id);
        usersMap.remove(id);
        usersByEmail.remove(removeUser.getEmail());

    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(email));
    }
}
