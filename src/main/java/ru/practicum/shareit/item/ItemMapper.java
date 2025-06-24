package ru.practicum.shareit.item;

public class ItemMapper {
    public static Item toItem(ItemRequestDTO itemDTO, Long ownerId) {
        return Item.builder()
                .name(itemDTO.getName())
                .description(itemDTO.getDescription())
                .available(itemDTO.getAvailable())
                .ownerId(ownerId)
                .build();
    }

    public static ItemResponseDTO toItemResponseDTO(Item item) {
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