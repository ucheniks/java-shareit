package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @Valid @RequestBody ItemRequestDTO itemRequestDTO) {
        log.info("Gateway: create item {} by user {}", itemRequestDTO, userId);
        return itemClient.createItem(userId, itemRequestDTO);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody @Valid ItemUpdateDTO itemUpdateDTO) {
        log.info("Gateway: update item id {} by user {}", itemId, userId);
        return itemClient.updateItem(userId, itemId, itemUpdateDTO);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @PathVariable Long itemId) {
        log.info("Gateway: get item id {}", itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Gateway: get all items by owner {}", userId);
        return itemClient.getAllItemsByOwner(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        log.info("Gateway: search items by text '{}'", text);
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @PathVariable Long itemId,
                                             @Valid @RequestBody CommentRequestDTO commentRequestDTO) {
        log.info("Gateway: add comment to item {} by user {}", itemId, userId);
        return itemClient.addComment(userId, itemId, commentRequestDTO);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@PathVariable Long itemId) {
        log.info("Gateway: delete item id {}", itemId);
        return itemClient.deleteItem(itemId);
    }
}
