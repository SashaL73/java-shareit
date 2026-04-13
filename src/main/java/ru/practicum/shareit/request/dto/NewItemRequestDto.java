package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewItemRequestDto {
    private String description;
    private Long idRequester;
    private LocalDateTime created;
}
