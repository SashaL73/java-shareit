package user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ru.practicum.shareit.ShareItGateway.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void findAllUsersShouldReturnList() throws Exception {
        Mockito.when(userClient.getAllUsers())
                .thenReturn(ResponseEntity.ok("listUserDto"));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string("listUserDto"));
    }

    @Test
    void findUserByIdShouldReturnUser() throws Exception {
        Mockito.when(userClient.getUser(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok("userDto"));

        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("userDto"));
    }

    @Test
    void createUserShouldReturnCreated() throws Exception {
        NewUserRequest request = new NewUserRequest();
        request.setName("TestName");
        request.setEmail("test@test.com");

        Mockito.when(userClient.createUser(Mockito.any()))
                .thenReturn(ResponseEntity.ok("userDto"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("userDto"));
    }

    @Test
    void updateUserShouldReturnUpdated() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("TestName");
        request.setEmail("test@test.com");

        Mockito.when(userClient.updateUser(Mockito.anyLong(), Mockito.any()))
                .thenReturn(ResponseEntity.ok("userDto"));

        mockMvc.perform(patch("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("userDto"));
    }

    @Test
    void deleteUserShouldReturnNoContent() throws Exception {
        Mockito.when(userClient.deleteUser(Mockito.anyLong()))
                .thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
