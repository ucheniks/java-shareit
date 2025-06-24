package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;

    @NotBlank(message = "Электронная почта пользователя не может быть пустой")
    @Email(message = "Электронная почта пользователя неподходящего формата")
    private String email;
}
