package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemResponseDTO createItem(
            @RequestHeader(USER_ID_HEADER) Long ownerId,
            @Valid @RequestBody ItemRequestDTO itemDTO) {
        log.info("Создание вещи: {} для пользователя {}", itemDTO, ownerId);
        return itemService.createItem(ownerId, itemDTO);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDTO updateItem(
            @RequestHeader(USER_ID_HEADER) Long ownerId,
            @PathVariable Long itemId,
            @RequestBody Map<String, Object> updates) {
        log.info("Обновление вещи ID {}: {} пользователем {}", itemId, updates, ownerId);
        return itemService.updateItem(itemId, ownerId, updates);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDTO getItemById(@PathVariable Long itemId) {
        log.info("Получение вещи по ID: {}", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemResponseDTO> getAllItemsByOwner(
            @RequestHeader(USER_ID_HEADER) Long ownerId) {
        log.info("Получение всех вещей владельца ID: {}", ownerId);
        return itemService.getAllItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemResponseDTO> searchAvailableItems(
            @RequestParam String text) {
        log.info("Поиск доступных вещей по тексту: {}", text);
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return itemService.searchAvailableItems(text);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        log.info("Удаление вещи ID: {}", itemId);
        itemService.deleteItem(itemId);
    }
}