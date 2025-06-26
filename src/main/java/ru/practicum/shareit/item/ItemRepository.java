package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();

    public Item addItem(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    public Item updateItem(Long itemId, Long ownerId, ItemUpdateDTO updates) {
        Item existingItem = getItemById(itemId);

        if (!existingItem.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }

        if (updates.getName() != null) {
            existingItem.setName(updates.getName());
        }
        if (updates.getDescription() != null) {
            existingItem.setDescription(updates.getDescription());
        }
        if (updates.getAvailable() != null) {
            existingItem.setAvailable(updates.getAvailable());
        }

        return existingItem;
    }

    public Item getItemById(Long id) {
        return Optional.ofNullable(items.get(id))
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: " + id));
    }

    public List<Item> findAllByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> ownerId.equals(item.getOwnerId()))
                .collect(Collectors.toList());
    }

    public List<Item> searchAvailableItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String searchText = text.toLowerCase();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchText) ||
                        item.getDescription().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
    }

    public void deleteItem(Long itemId) {
        items.remove(itemId);
    }

    private Long getNextId() {
        long maxId = items.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++maxId;
    }
}