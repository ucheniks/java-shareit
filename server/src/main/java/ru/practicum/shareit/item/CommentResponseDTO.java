package ru.practicum.shareit.item;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
