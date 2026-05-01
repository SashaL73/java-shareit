package item;

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
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ru.practicum.shareit.ShareItGateway.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void getAllUserItemsShouldReturnList() throws Exception {
        Mockito.when(itemClient.getAllUserItem(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok("list"));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().string("list"));
    }

    @Test
    void getItemByIdShouldReturnItem() throws Exception {
        Mockito.when(itemClient.getItemById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok("itemDto"));

        mockMvc.perform(get("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().string("itemDto"));
    }

    @Test
    void saveItemShouldReturnCreatedItem() throws Exception {
        NewItemRequest request = new NewItemRequest();
        request.setName("Test");
        request.setDescription("Description");
        request.setAvailable(true);

        Mockito.when(itemClient.createItem(Mockito.anyLong(), Mockito.any()))
                .thenReturn(ResponseEntity.ok("itemDto"));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("itemDto"));
    }

    @Test
    void updateItemShouldReturnUpdatedItem() throws Exception {
        UpdateItemRequest request = new UpdateItemRequest();

        Mockito.when(itemClient.updateItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(ResponseEntity.ok("itemDto"));

        mockMvc.perform(patch("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("itemDto"));
    }

    @Test
    void searchItemShouldReturnResults() throws Exception {
        Mockito.when(itemClient.searchItem(Mockito.anyString()))
                .thenReturn(ResponseEntity.ok("listItems"));

        mockMvc.perform(get("/items/search")
                        .param("text", "searchText"))
                .andExpect(status().isOk())
                .andExpect(content().string("listItems"));
    }

    @Test
    void saveCommentsShouldReturnComment() throws Exception {
        NewCommentRequest request = new NewCommentRequest();
        request.setText("Text");

        Mockito.when(itemClient.createComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(ResponseEntity.ok("commentDto"));

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("commentDto"));
    }
}
