package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.UserStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.user.dto.UserDtoForBooking;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    private static final String PATH = "/bookings";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper mapper;

    private BookingDto responseDto = BookingDto.builder()
            .id(1L)
            .start(LocalDateTime.of(2026, 5, 1, 10, 0))
            .end(LocalDateTime.of(2026, 5, 2, 10, 0))
            .status(BookingStatus.WAITING)
            .booker(new UserDtoForBooking())
            .item(new ItemDtoForBooking())
            .build();

    private NewBookingRequest requestDto = NewBookingRequest.builder()
            .itemId(1L)
            .start(LocalDateTime.of(2026, 5, 1, 10, 0))
            .end(LocalDateTime.of(2026, 5, 2, 10, 0))
            .build();

    @Test
    void createNewBookingShouldReturnBooking() throws Exception {

        Mockito.when(bookingService.createBooking(Mockito.anyLong(), Mockito.any(NewBookingRequest.class)))
                .thenReturn(responseDto);

        String start = responseDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String end = responseDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        mockMvc.perform(post(PATH)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.start").value(start))
                .andExpect(jsonPath("$.end").value(end))
                .andExpect(jsonPath("$.status").value(responseDto.getStatus().toString()))
                .andExpect(jsonPath("$.booker").isMap())
                .andExpect(jsonPath("$.item").isMap());
    }

    @Test
    void approvedBookingShouldReturnApprovedBooking() throws Exception {
        BookingDto bookingDto = responseDto;
        bookingDto.setStatus(BookingStatus.APPROVED);
        Mockito.when(bookingService.approvedBooking(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(bookingDto);

        String start = responseDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String end = responseDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        mockMvc.perform(patch(PATH + "/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.start").value(start))
                .andExpect(jsonPath("$.end").value(end))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.booker").isMap())
                .andExpect(jsonPath("$.item").isMap());
    }

    @Test
    void getAllBookingsByUserShouldReturnListBooking() throws Exception {
        List<BookingDto> bookingDtoList = List.of(responseDto);

        Mockito.when(bookingService.findAllBookingByUserOrItemOwner(Mockito.anyLong(), Mockito.any(BookingState.class),
                        Mockito.any(UserStatus.class)))
                .thenReturn(bookingDtoList);

        String start = responseDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String end = responseDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        mockMvc.perform(get(PATH)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].start").value(start))
                .andExpect(jsonPath("$[0].end").value(end))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[0].booker").isMap())
                .andExpect(jsonPath("$[0].item").isMap());

    }

    @Test
    void createBookingShouldReturnBadRequestWhenStartIsAfterEnd() throws Exception {
        NewBookingRequest invalidRequest = NewBookingRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 5, 2, 10, 0))  // start после end
                .end(LocalDateTime.of(2026, 5, 1, 10, 0))
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBookingShouldReturnBadRequestWhenEndIsBeforeStart() throws Exception {
        NewBookingRequest invalidRequest = NewBookingRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2026, 5, 2, 10, 0))  // start после end
                .end(LocalDateTime.of(2023, 5, 1, 10, 0))
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBookingShouldReturnBadRequestWhenItemIdIsNull() throws Exception {
        NewBookingRequest invalidRequest = NewBookingRequest.builder()
                .itemId(null)
                .start(LocalDateTime.of(2026, 5, 1, 10, 0))
                .end(LocalDateTime.of(2026, 5, 2, 10, 0))
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
