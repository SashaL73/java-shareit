package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {
    private static final String PATH = "/requests";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper mapper;

    private ItemRequestDto responseDto = ItemRequestDto.builder()
            .id(1L)
            .description("Description")
            .created(LocalDateTime.of(2026, 4, 20, 10, 0))
            .items(new ArrayList<>())
            .build();

    private NewItemRequestDto requestDto = NewItemRequestDto.builder()
            .description("Description")
            .build();

    @Test
    void saveRequestShouldReturnRequest() throws Exception {
        Mockito.when(itemRequestService.createRequest(Mockito.anyLong(), Mockito.any(NewItemRequestDto.class)))
                .thenReturn(responseDto);

        String created = responseDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        mockMvc.perform(post(PATH)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.created").value(created))
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void getAllRequestsByUserShouldReturnListRequest() throws Exception {
        List<ItemRequestDto> itemRequestDtos = List.of(responseDto);
        Mockito.when(itemRequestService.getAllUserRequests(Mockito.anyLong()))
                .thenReturn(itemRequestDtos);

        String created = responseDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        mockMvc.perform(get(PATH)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Description"))
                .andExpect(jsonPath("$[0].created").value(created))
                .andExpect(jsonPath("$[0].items").isArray());
    }

    @Test
    void getAllRequestsShouldReturnListRequest() throws Exception {
        List<ItemRequestDto> itemRequestDtos = List.of(responseDto);
        Mockito.when(itemRequestService.getAllRequests(Mockito.anyLong()))
                .thenReturn(itemRequestDtos);

        String created = responseDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        mockMvc.perform(get(PATH + "/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Description"))
                .andExpect(jsonPath("$[0].created").value(created))
                .andExpect(jsonPath("$[0].items").isArray());
    }

    @Test
    void getRequestShouldReturnRequest() throws Exception {
        Mockito.when(itemRequestService.getRequestById(Mockito.anyLong()))
                .thenReturn(responseDto);

        String created = responseDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        mockMvc.perform(get(PATH + "/{requestId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.created").value(created))
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void shouldReturnBadRequestForInvalidRequest() throws Exception {
        NewItemRequestDto request = new NewItemRequestDto();

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
