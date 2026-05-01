package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookItemRequestDto {
    @NotNull(message = "itemId должен быть указан")
    @Positive(message = "Id должен быть положительным")
    private long itemId;
    @FutureOrPresent
    @NotNull(message = "дата начала бронирования должна быть указана")
    private LocalDateTime start;
    @Future
    @NotNull(message = "дата окончания бронирования должна быть указана")
    private LocalDateTime end;
}
