package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.user.dto.UserDtoForBooking;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoJsonTest {

    private final JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2026, 5, 1, 10, 0))
                .end(LocalDateTime.of(2026, 5, 2, 10, 0))
                .status(BookingStatus.WAITING)
                .booker(UserDtoForBooking.builder()
                        .id(1L)
                        .name("Test")
                        .build())
                .item(ItemDtoForBooking.builder()
                        .id(1L)
                        .name("TestItem")
                        .build())
                .build();

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2026-05-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2026-05-02T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo("WAITING");
        assertThat(result).extractingJsonPathStringValue("$.booker.name")
                .isEqualTo("Test");
        assertThat(result).extractingJsonPathStringValue("$.item.name")
                .isEqualTo("TestItem");

    }

    @Test
    void testBookingDtoDeserialization() throws Exception {
        String content = "{\"id\":1,\"start\":\"2026-05-01T10:00:00\",\"end\":\"2026-05-02T10:00:00\",\"status\":\"WAITING\"," +
                "\"booker\":{\"id\":1,\"name\":\"Test\"},\"item\":{\"id\":1,\"name\":\"TestItem\"}}";

        BookingDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2026, 5, 1, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2026, 5, 2, 10, 0));

        assertThat(dto.getBooker().getId()).isEqualTo(1L);
        assertThat(dto.getBooker().getName()).isEqualTo("Test");

        assertThat(dto.getItem().getId()).isEqualTo(1L);
        assertThat(dto.getItem().getName()).isEqualTo("TestItem");
    }
}

