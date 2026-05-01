package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void createItemShouldReturnCreatedItem() throws Exception {
        NewItemRequest request = new NewItemRequest();
        request.setName("Test");
        request.setDescription("Description");
        request.setAvailable(true);

        ItemDto responseDto = new ItemDto();
        responseDto.setId(1L);
        responseDto.setName("Test");
        responseDto.setDescription("Description");
        responseDto.setAvailable(true);

        Mockito.when(itemService.saveItem(Mockito.anyLong(), Mockito.any(NewItemRequest.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void getItemShouldReturnItem() throws Exception {
        ItemOwnerDto itemOwnerDto = new ItemOwnerDto();
        itemOwnerDto.setName("Test");
        itemOwnerDto.setId(1L);
        itemOwnerDto.setDescription("Description");
        itemOwnerDto.setAvailable(true);

        Mockito.when(itemService.findItemById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemOwnerDto);

        mockMvc.perform(get("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.available").value(true));

    }

    @Test
    void getAllUserItemsReturnItemsList() throws Exception {
        ItemOwnerDto itemOwnerDto = new ItemOwnerDto();
        itemOwnerDto.setName("Test");
        itemOwnerDto.setId(1L);
        itemOwnerDto.setDescription("Description");
        itemOwnerDto.setAvailable(true);
        itemOwnerDto.setLastBooking(new BookingShortDto(1L, 1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1)));
        itemOwnerDto.setNextBooking(new BookingShortDto(1L, 1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)));

        List<ItemOwnerDto> listOwnerItems = List.of(itemOwnerDto);

        Mockito.when(itemService.findAllUserItems(Mockito.anyLong()))
                .thenReturn(listOwnerItems);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test"))
                .andExpect(jsonPath("$[0].description").value("Description"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[0].lastBooking.id").value(1L))
                .andExpect(jsonPath("$[0].lastBooking.bookerId").value(1L))
                .andExpect(jsonPath("$[0].nextBooking.id").value(1L))
                .andExpect(jsonPath("$[0].nextBooking.bookerId").value(1L));


    }

    @Test
    void updateItemShouldReturnUpdatedItem() throws Exception {
        UpdateItemRequest request = new UpdateItemRequest();
        request.setName("Test");
        request.setDescription("Description");
        request.setAvailable(false);

        ItemDto updatedItem = new ItemDto();
        updatedItem.setId(1L);
        updatedItem.setName(request.getName());
        updatedItem.setDescription(request.getDescription());
        updatedItem.setAvailable(request.getAvailable());

        Mockito.when(itemService.updateItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(UpdateItemRequest.class)))
                .thenReturn(updatedItem);

        mockMvc.perform(patch("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void searchItemShouldReturnItem() throws Exception {
        ItemDto responseDto = new ItemDto();
        responseDto.setId(1L);
        responseDto.setName("Test");
        responseDto.setDescription("Description");
        responseDto.setAvailable(true);

        List<ItemDto> itemDtoList = List.of(responseDto);

        Mockito.when(itemService.searchItem(Mockito.anyString()))
                .thenReturn(itemDtoList);

        mockMvc.perform(get("/items/search")
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test"))
                .andExpect(jsonPath("$[0].description").value("Description"))
                .andExpect(jsonPath("$[0].available").value(true));

    }

    @Test
    void saveCommentShouldReturnComment() throws Exception {
        NewCommentRequest requestDto = new NewCommentRequest();

        requestDto.setText("Test");

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Test");
        commentDto.setAuthorName("Name");
        commentDto.setCreated(LocalDateTime.now().toString());

        Mockito.when(itemService.saveComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(NewCommentRequest.class)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Test"))
                .andExpect(jsonPath("$.authorName").value("Name"))
                .andExpect(jsonPath("$.created").value(commentDto.getCreated()));
    }

    @Test
    void shouldReturnBadRequestForInvalidNewItem() throws Exception {
        NewItemRequest request = new NewItemRequest();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
