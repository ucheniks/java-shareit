package ru.practicum.shareit.item;


public class CommentMapper {
    public static CommentResponseDTO toDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
