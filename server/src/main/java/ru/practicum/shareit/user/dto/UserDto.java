package ru.practicum.shareit.user.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserDto {
    private Long id;
    private String email;
    private String name;
}
