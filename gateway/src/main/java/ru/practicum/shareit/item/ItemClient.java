package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;


import java.util.Map;

@Component
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .build());
    }

    public ResponseEntity<Object> createItem(Long userId, ItemRequestDTO itemRequestDTO) {
        return post("", userId, itemRequestDTO);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemUpdateDTO itemUpdateDTO) {
        return patch("/" + itemId, userId, itemUpdateDTO);
    }

    public ResponseEntity<Object> getItemById(Long itemId, Long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllItemsByOwner(Long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> searchItems(String text) {
        if (text.isBlank()) {
            return ResponseEntity.ok().body(null);
        }
        Map<String, Object> parameters = Map.of("text", text);
        return get("/search?text={text}", null, parameters);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentRequestDTO commentRequestDTO) {
        return post("/" + itemId + "/comment", userId, commentRequestDTO);
    }

    public ResponseEntity<Object> deleteItem(Long itemId) {
        return delete("/" + itemId);
    }
}
