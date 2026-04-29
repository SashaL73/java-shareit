package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    private static final String PATH = "/users";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper mapper;

    private UserDto responseDto = UserDto.builder()
            .id(1L)
            .name("Test")
            .email("test@test.com")
            .build();

    private NewUserRequest requestDto = NewUserRequest.builder()
            .name("Test")
            .email("test@test.com")
            .build();

    @Test
    void findAllUsersShouldReturnListUsers() throws Exception {
        List<UserDto> userDtoList = List.of(responseDto);
        Mockito.when(userService.getAllUsers())
                .thenReturn(userDtoList);


        mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test"))
                .andExpect(jsonPath("$[0].email").value("test@test.com"));
    }

    @Test
    void findUserByIdShouldReturnUser() throws Exception {
        Mockito.when(userService.getUserById(Mockito.anyLong()))
                .thenReturn(responseDto);

        mockMvc.perform(get(PATH + "/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void createUserShouldReturnUser() throws Exception {
        Mockito.when(userService.saveUser(Mockito.any(NewUserRequest.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post(PATH)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void updateUserShouldReturnUpdatedUser() throws Exception {
        Mockito.when(userService.updateUser(Mockito.anyLong(), Mockito.any(UpdateUserRequest.class)))
                .thenReturn(responseDto);

        mockMvc.perform(patch(PATH + "/{id}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void deleteUserShouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(userService).deleteUser(Mockito.anyLong());

        mockMvc.perform(delete(PATH + "/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void createUserShouldReturnConflict() throws Exception {
        NewUserRequest newUserRequest = NewUserRequest.builder()
                .name("Test User")
                .email("test@test.com")
                .build();

        Mockito.when(userService.saveUser(Mockito.any(NewUserRequest.class)))
                .thenThrow(new ConflictException("Пользователь с email test@test.com существует"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newUserRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnBadRequestForInvalidUser() throws Exception {
        NewUserRequest request = new NewUserRequest();
        request.setName("");
        request.setEmail("test.com");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldReturnBadRequestForInvalidUpdate() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("test.com");

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
