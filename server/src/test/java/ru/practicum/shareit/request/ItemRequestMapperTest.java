package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemShortResponseDTO;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    @Test
    void toItemRequest_createsEntityCorrectly() {
        User user = new User();
        user.setId(5L);

        ItemRequestReqDTO dto = new ItemRequestReqDTO("description");

        ItemRequest entity = ItemRequestMapper.toItemRequest(dto, user);

        assertEquals(dto.getDescription(), entity.getDescription());
        assertEquals(user, entity.getRequestor());
        assertNotNull(entity.getCreated());
    }

    @Test
    void toItemRequestResponseDTO_withItems() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("desc");
        request.setCreated(LocalDateTime.now());

        ItemShortResponseDTO itemDto = ItemShortResponseDTO.builder()
                .id(100L)
                .name("itemName")
                .ownerId(10L)
                .build();

        ru.practicum.shareit.item.Item item = new ru.practicum.shareit.item.Item();
        item.setId(100L);
        item.setName("itemName");
        ru.practicum.shareit.user.User owner = new ru.practicum.shareit.user.User();
        owner.setId(10L);
        item.setOwner(owner);

        request.setItems(List.of(item));

        ItemRequestResponseDTO dto = ItemRequestMapper.toItemRequestResponseDTO(request);

        assertEquals(request.getId(), dto.getId());
        assertEquals(request.getDescription(), dto.getDescription());
        assertNotNull(dto.getCreated());
        assertFalse(dto.getItems().isEmpty());

        ItemShortResponseDTO mappedItem = dto.getItems().get(0);
        assertEquals(item.getId(), mappedItem.getId());
        assertEquals(item.getName(), mappedItem.getName());
        assertEquals(owner.getId(), mappedItem.getOwnerId());
    }

    @Test
    void toItemRequestResponseDTO_withNoItems() {
        ItemRequest request = new ItemRequest();
        request.setId(2L);
        request.setDescription("desc2");
        request.setCreated(LocalDateTime.now());
        request.setItems(null);

        ItemRequestResponseDTO dto = ItemRequestMapper.toItemRequestResponseDTO(request);

        assertNotNull(dto);
        assertNotNull(dto.getItems());
        assertTrue(dto.getItems().isEmpty());
    }
}
