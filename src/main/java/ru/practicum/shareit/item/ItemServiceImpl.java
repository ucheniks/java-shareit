package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemResponseDTO createItem(Long ownerId, ItemRequestDTO itemDTO) {
        if (!userService.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с ID " + ownerId + " не найден");
        }
        Item item = ItemMapper.toItem(itemDTO, ownerId);
        Item savedItem = itemRepository.addItem(item);
        return ItemMapper.toItemResponseDTO(savedItem);
    }

    @Override
    public ItemResponseDTO updateItem(Long itemId, Long ownerId, Map<String, Object> updates) {
        Item updatedItem = itemRepository.updateItem(itemId, ownerId, updates);
        return ItemMapper.toItemResponseDTO(updatedItem);
    }

    @Override
    public ItemResponseDTO getItemById(Long itemId) {
        Item item = itemRepository.getItemById(itemId);
        return ItemMapper.toItemResponseDTO(item);
    }

    @Override
    public List<ItemResponseDTO> getAllItemsByOwner(Long ownerId) {
        return itemRepository.findAllByOwnerId(ownerId).stream()
                .map(ItemMapper::toItemResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDTO> searchAvailableItems(String text) {
        return itemRepository.searchAvailableItems(text).stream()
                .map(ItemMapper::toItemResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long itemId) {
        itemRepository.deleteItem(itemId);
    }
}