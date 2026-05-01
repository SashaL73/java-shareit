package request;

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
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ru.practicum.shareit.ShareItGateway.class)
@AutoConfigureMockMvc
public class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestClient requestClient;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void saveRequestShouldReturnCreatedRequest() throws Exception {
        NewItemRequestDto requestDto = new NewItemRequestDto();
        requestDto.setDescription("Description");

        Mockito.when(requestClient.saverRequest(Mockito.anyLong(), Mockito.any()))
                .thenReturn(ResponseEntity.ok("requestDto"));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("requestDto"));
    }

    @Test
    void getAllRequestsByUserShouldReturnList() throws Exception {
        Mockito.when(requestClient.getAllUserRequests(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok("requestList"));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().string("requestList"));
    }

    @Test
    void getAllRequestsShouldReturnList() throws Exception {
        Mockito.when(requestClient.getAllRequests(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok("requestList"));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().string("requestList"));
    }

    @Test
    void getRequestByIdShouldReturnRequest() throws Exception {
        Mockito.when(requestClient.getRequest(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok("requestDto"));

        mockMvc.perform(get("/requests/{requestId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("requestDto"));
    }

}
