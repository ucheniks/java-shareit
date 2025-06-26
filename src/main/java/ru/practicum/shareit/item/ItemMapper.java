package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ItemMapper {
    public Item toItem(ItemRequestDTO dto, Long ownerId) {
        return Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .ownerId(ownerId)
                .build();
    }

    public ItemResponseDTO toItemResponseDTO(Item item) {
        return new ItemResponseDTO(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwnerId(),
                item.getRequestId()
        );
    }
}