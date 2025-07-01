package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemResponseDTO createItem(Long ownerId, ItemRequestDTO itemDTO);

    ItemResponseDTO updateItem(Long itemId, Long ownerId, ItemUpdateDTO updateItem);

    ItemResponseDTO getItemById(Long itemId);

    List<ItemResponseDTO> getAllItemsByOwner(Long ownerId);

    List<ItemResponseDTO> searchAvailableItems(String text);

    void deleteItem(Long itemId);

    CommentResponseDTO addComment(Long userId, Long itemId, String text);
}