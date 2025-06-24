package ru.practicum.shareit.item;

import java.util.List;
import java.util.Map;

public interface ItemService {
    ItemResponseDTO createItem(Long ownerId, ItemRequestDTO itemDTO);

    ItemResponseDTO updateItem(Long itemId, Long ownerId, Map<String, Object> updates);

    ItemResponseDTO getItemById(Long itemId);

    List<ItemResponseDTO> getAllItemsByOwner(Long ownerId);

    List<ItemResponseDTO> searchAvailableItems(String text);

    void deleteItem(Long itemId);
}