package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingShortDto {
    private Long id;
    private Long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;

    public BookingShortDto(Long id, Long bookerId, LocalDateTime start, LocalDateTime end) {
        this.id = id;
        this.bookerId = bookerId;
        this.start = start;
        this.end = end;
    }
}
