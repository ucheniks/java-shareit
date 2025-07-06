package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.ItemShortResponseDTO;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequest toItemRequest(ItemRequestReqDTO dto, User requestor) {
        return ItemRequest.builder()
                .description(dto.getDescription())
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
    }

    public ItemRequestResponseDTO toItemRequestResponseDTO(ItemRequest itemRequest) {
        List<ItemShortResponseDTO> itemDTOs = Collections.emptyList();
        if (itemRequest.getItems() != null) {
            itemDTOs = itemRequest.getItems().stream()
                    .map(item -> ItemShortResponseDTO.builder()
                            .id(item.getId())
                            .name(item.getName())
                            .ownerId(item.getOwner().getId())
                            .build())
                    .toList();
        }

        return ItemRequestResponseDTO.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemDTOs)
                .build();
    }
}