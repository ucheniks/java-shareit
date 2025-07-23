package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Captor
    private ArgumentCaptor<ItemRequestDTO> requestCaptor;

    @Captor
    private ArgumentCaptor<ItemUpdateDTO> updateCaptor;

    private final ItemRequestDTO request = new ItemRequestDTO("Дрель", "Аккумуляторная", true, null);
    private final ItemResponseDTO response = ItemResponseDTO.builder()
            .id(1L)
            .name("Дрель")
            .description("Аккумуляторная")
            .available(true)
            .ownerId(1L)
            .build();

    @Test
    void create() throws Exception {
        when(itemService.createItem(eq(1L), any())).thenReturn(response);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Дрель")));

        verify(itemService).createItem(eq(1L), requestCaptor.capture());
        assertEquals("Дрель", requestCaptor.getValue().getName());
    }

    @Test
    void createNotFound() throws Exception {
        when(itemService.createItem(eq(999L), any()))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 999L)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователь не найден"));
    }

    @Test
    void createInvalidItem() throws Exception {
        ItemRequestDTO invalidRequest = new ItemRequestDTO("Некорректное имя", "Описание", true, null);

        when(itemService.createItem(eq(1L), any()))
                .thenThrow(new ValidationException("Ошибка валидации"));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации"));
    }

    @Test
    void update() throws Exception {
        ItemUpdateDTO update = new ItemUpdateDTO();
        update.setName("Дрель+");
        ItemResponseDTO updated = ItemResponseDTO.builder()
                .id(1L)
                .name("Дрель+")
                .description("Аккумуляторная")
                .available(true)
                .ownerId(1L)
                .build();

        when(itemService.updateItem(eq(1L), eq(1L), any())).thenReturn(updated);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(update))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Дрель+"));

        verify(itemService).updateItem(eq(1L), eq(1L), updateCaptor.capture());
        assertEquals("Дрель+", updateCaptor.getValue().getName());
    }

    @Test
    void updateNotFound() throws Exception {
        ItemUpdateDTO update = new ItemUpdateDTO();
        update.setName("Дрель+");

        when(itemService.updateItem(eq(1L), eq(999L), any()))
                .thenThrow(new NotFoundException("Недостаточно прав"));

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 999L)
                        .content(objectMapper.writeValueAsString(update))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Недостаточно прав"));
    }

    @Test
    void getById() throws Exception {
        when(itemService.getItemById(1L)).thenReturn(response);

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void getByIdNotFound() throws Exception {
        when(itemService.getItemById(999L)).thenThrow(new NotFoundException("Вещь не найдена"));

        mockMvc.perform(get("/items/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Вещь не найдена"));
    }

    @Test
    void getAllByOwner() throws Exception {
        when(itemService.getAllItemsByOwner(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Дрель"));
    }

    @Test
    void search() throws Exception {
        when(itemService.searchAvailableItems("дрель")).thenReturn(List.of(response));

        mockMvc.perform(get("/items/search")
                        .param("text", "дрель"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Дрель"));
    }

    @Test
    void searchEmpty() throws Exception {
        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void comment() throws Exception {
        LocalDateTime created = LocalDateTime.of(2005, 6, 19, 0, 0);
        CommentResponseDTO comment = CommentResponseDTO.builder()
                .id(1L)
                .text("Хорошая дрель!")
                .authorName("Георгий")
                .created(created)
                .build();

        when(itemService.addComment(eq(1L), eq(1L), any(String.class)))
                .thenReturn(comment);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(new CommentRequestDTO("Хорошая дрель!")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Хорошая дрель!"))
                .andExpect(jsonPath("$.authorName").value("Георгий"))
                .andExpect(jsonPath("$.created").value("2005-06-19T00:00:00"));
    }

    @Test
    void commentBadRequest() throws Exception {
        when(itemService.addComment(eq(1L), eq(1L), any(String.class)))
                .thenThrow(new ValidationException("Нельзя комментировать без брони"));

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(new CommentRequestDTO("Текст")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Нельзя комментировать без брони"));
    }

    @Test
    void deleteItem() throws Exception {
        doNothing().when(itemService).deleteItem(1L);

        mockMvc.perform(delete("/items/1"))
                .andExpect(status().isNoContent());

        verify(itemService).deleteItem(1L);
    }
}
