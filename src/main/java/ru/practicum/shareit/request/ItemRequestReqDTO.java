package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestReqDTO {
 @NotBlank(message = "Описание запроса не может быть пустым")
    private String description;
}
