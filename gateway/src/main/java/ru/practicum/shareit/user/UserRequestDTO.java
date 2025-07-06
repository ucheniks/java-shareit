package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRequestDTO {
    @NotBlank(message = "Имя обязательно и не может быть пустым")
    private String name;

    @NotBlank(message = "Email обязателен и ен может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;
}
