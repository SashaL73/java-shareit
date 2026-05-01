package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class NewUserRequest {
    @NotEmpty(message = "Имя должно быть указано")
    private String name;
    @Email(message = "Некорректный формат email")
    @NotEmpty(message = "Email должен быть указан")
    private String email;
}
