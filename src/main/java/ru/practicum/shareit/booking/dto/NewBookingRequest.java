package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewBookingRequest {
    @NotNull(message = "Id вещи должен быть указан")
    private Long itemId;
    @NotNull(message = "Дата начала бронирования должна быть указана")
    @FutureOrPresent(message = "Дата начала бронирования должна быть в будущем или сейчас")
    private LocalDateTime start;
    @NotNull(message = "Дата конца бронирования должна быть указана")
    @Future(message = "Дата конца бронирования должна быть в будущем или сейчас")
    private LocalDateTime end;
}
