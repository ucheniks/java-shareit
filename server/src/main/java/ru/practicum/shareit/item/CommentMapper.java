package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommentMapper {
    public CommentResponseDTO toDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
