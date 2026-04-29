package ru.practicum.shareit.user.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserDtoForBooking {
    private Long id;
    private String name;
}
