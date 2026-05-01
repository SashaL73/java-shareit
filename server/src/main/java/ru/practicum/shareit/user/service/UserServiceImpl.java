package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto getUserById(Long id) {
        log.debug("Получение пользователя по id={}", id);
        return UserMapper.mapToUserDto(findUserOrThrow(id));
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.debug("Получение списка всех пользователей");
        return userRepository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto saveUser(NewUserRequest newUserRequest) {
        log.info("Создание пользователя с email={}", newUserRequest.getEmail());
        checkEmail(newUserRequest.getEmail(), null);
        User user = UserMapper.mapToUser(newUserRequest);
        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(Long id, UpdateUserRequest request) {
        log.info("Обновление пользователя id={}", id);
        User user = findUserOrThrow(id);
        checkEmail(request.getEmail(), id);
        User updatedUser = UserMapper.updateUserFields(user, request);
        User saved = userRepository.save(updatedUser);
        log.info("Пользователь обновлён id={}", id);
        return UserMapper.mapToUserDto(saved);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Удаление пользователя id={}", id);
        findUserOrThrow(id);
        userRepository.deleteById(id);
        log.info("Пользователь удалён id={}", id);
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Пользователь не неайден с id={}", id);
                    return new NotFoundException("Пользователь с id " + id + " не найден");
                });
    }

    private void checkEmail(String email, Long userId) {
        if (userRepository.findByEmailAndIdNot(email, userId).isPresent()) {
            log.warn("Пользователь с email={} существует", email);
            throw new ConflictException("Пользователь с email " + email + " существует");
        }
    }

}
