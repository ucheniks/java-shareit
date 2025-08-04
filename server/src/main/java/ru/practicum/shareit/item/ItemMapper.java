package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {
    public Item toItem(ItemRequestDTO dto, User owner, ItemRequest request) {
        return Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .owner(owner)
                .request(request)
                .build();
    }

    public Item toItemFromUpdate(ItemUpdateDTO dto, User owner, Long itemId) {
        return Item.builder()
                .id(itemId)
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .owner(owner)
                .build();
    }

    public ItemResponseDTO toItemResponseDTO(Item item) {
        List<CommentResponseDTO> commentDTOs = null;
        if (item.getComments() != null) {
            commentDTOs = item.getComments().stream()
                    .map(comment -> CommentResponseDTO.builder()
                            .id(comment.getId())
                            .text(comment.getText())
                            .authorName(comment.getAuthor().getName())
                            .created(comment.getCreated())
                            .build())
                    .collect(Collectors.toList());
        }

        return ItemResponseDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .comments(commentDTOs)
                .build();
    }
}