package booking;

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
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ru.practicum.shareit.ShareItGateway.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void bookItemShouldReturnResponseFromClient() throws Exception {
        BookItemRequestDto requestDto = BookItemRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2026, 5, 1, 10, 0))
                .end(LocalDateTime.of(2026, 5, 2, 10, 0))
                .build();

        ResponseEntity<Object> clientResponse = ResponseEntity.ok("SUCCESS");
        Mockito.when(bookingClient.bookItem(Mockito.anyLong(), Mockito.any()))
                .thenReturn(clientResponse);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("SUCCESS"));
    }

    @Test
    void getBookingsShouldReturnClientResponse() throws Exception {
        ResponseEntity<Object> clientResponse = ResponseEntity.ok("list");
        Mockito.when(bookingClient.getBookings(Mockito.anyLong(), Mockito.any(BookingState.class), Mockito.any(),
                        Mockito.any()))
                .thenReturn(clientResponse);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("list"));

        Mockito.verify(bookingClient).getBookings(1L, BookingState.ALL, 0, 10);
    }

    @Test
    void getBookingShouldReturnClientResponse() throws Exception {
        ResponseEntity<Object> clientResponse = ResponseEntity.ok("bookingDto");
        Mockito.when(bookingClient.getBooking(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(clientResponse);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("bookingDto"));

        Mockito.verify(bookingClient).getBooking(1L, 1L);
    }

    @Test
    void approvedBookingShouldReturnClientResponse() throws Exception {
        ResponseEntity<Object> clientResponse = ResponseEntity.ok("approved");
        Mockito.when(bookingClient.approvedBooking(Mockito.anyLong(), Mockito.anyLong(),
                        Mockito.anyBoolean()))
                .thenReturn(clientResponse);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("approved"));

        Mockito.verify(bookingClient).approvedBooking(1L, 1L, true);
    }

    @Test
    void getAllBookingsByOwnerShouldReturnClientResponse() throws Exception {
        ResponseEntity<Object> clientResponse = ResponseEntity.ok("OWNER_LIST");
        Mockito.when(bookingClient.findAllBookingByUserOrItemOwner(Mockito.anyLong(), Mockito.any(BookingState.class)))
                .thenReturn(clientResponse);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OWNER_LIST"));

        Mockito.verify(bookingClient).findAllBookingByUserOrItemOwner(1L, BookingState.ALL);
    }

}
