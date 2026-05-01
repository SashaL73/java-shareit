package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UserServiceIntegrationTest {

    private final UserRepository userRepository;
    private final UserService userService;

    @Test
    void updateUserShouldReturnUpdatedUser() {
        User user = new User();
        user.setName("Test");
        user.setEmail("test@test.com");
        user = userRepository.save(user);

        userRepository.save(user);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();

        updateUserRequest.setName("UpdatedTest");
        updateUserRequest.setEmail("UpdatedEmail");

        UserDto userDto = userService.updateUser(user.getId(), updateUserRequest);

        Assertions.assertEquals(userDto.getName(), updateUserRequest.getName());
        Assertions.assertEquals(userDto.getEmail(), updateUserRequest.getEmail());

    }

    @Test
    void createUserShouldReturnSavedUser() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Test");
        request.setEmail("test@test.com");

        UserDto userDto = userService.saveUser(request);

        Assertions.assertNotNull(userDto.getId());
        Assertions.assertEquals("Test", userDto.getName());
        Assertions.assertEquals("test@test.com", userDto.getEmail());
    }

    @Test
    void deleteUserShouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        assertThrows(NotFoundException.class, () -> userService.deleteUser(999L));
    }

    @Test
    void deleteUserShouldRemoveUser() {
        User user = new User();
        user.setName("Test");
        user.setEmail("test@test.com");
        user = userRepository.save(user);

        userService.deleteUser(user.getId());

        Assertions.assertFalse(userRepository.findById(user.getId()).isPresent());
    }

    @Test
    void updateUserShouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Test");
        request.setEmail("test@test.com");

        assertThrows(NotFoundException.class, () -> userService.updateUser(999L, request));
    }

    @Test
    void updateUserShouldUpdateOnlyName() {
        User user = new User();
        user.setName("Test");
        user.setEmail("test@test.com");
        user = userRepository.save(user);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("NewTest");

        UserDto userDto = userService.updateUser(user.getId(), request);

        Assertions.assertEquals("NewTest", userDto.getName());
        Assertions.assertEquals("test@test.com", userDto.getEmail());
    }

}
