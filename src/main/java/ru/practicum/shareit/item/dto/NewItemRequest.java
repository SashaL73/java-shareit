package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewItemRequest {
    @NotEmpty(message = "Имя вещи должно быть указано")
    private String name;
    @NotEmpty(message = "Описание Вещи должно быть указано")
    private String description;
    @NotNull(message = "Статус должен быть указан")
    private Boolean available;
}
