package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUserById(Long id);

    List<UserDto> getAllUsers();

    UserDto saveUser(NewUserRequest newUserRequest);

    UserDto updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);
}
