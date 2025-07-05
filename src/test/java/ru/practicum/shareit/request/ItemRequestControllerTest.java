package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Captor
    private ArgumentCaptor<ItemRequestReqDTO> requestCaptor;

    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final ItemRequestReqDTO validRequest = new ItemRequestReqDTO("Нужна дрель");
    private final ItemRequestResponseDTO responseDto = ItemRequestResponseDTO.builder()
            .id(1L)
            .description("Нужна дрель")
            .created(LocalDateTime.of(2005, 6, 19, 0, 0))
            .build();

    @Test
    void createRequest_return200() throws Exception {
        when(itemRequestService.createRequest(anyLong(), any())).thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header(USER_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(validRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Нужна дрель")));

        verify(itemRequestService).createRequest(eq(1L), requestCaptor.capture());
        assertEquals("Нужна дрель", requestCaptor.getValue().getDescription());
    }

    @Test
    void createRequest_return400_blankDescription() throws Exception {
        ItemRequestReqDTO invalidRequest = new ItemRequestReqDTO("");

        mockMvc.perform(post("/requests")
                        .header(USER_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Описание запроса не может быть пустым")));
    }

    @Test
    void createRequest_return404_userNotFound() throws Exception {
        when(itemRequestService.createRequest(anyLong(), any()))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(post("/requests")
                        .header(USER_HEADER, 999L)
                        .content(objectMapper.writeValueAsString(validRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Пользователь не найден")));
    }

    @Test
    void getUserRequests_return200_withData() throws Exception {
        when(itemRequestService.getUserRequests(anyLong())).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/requests")
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is("Нужна дрель")));
    }

    @Test
    void getUserRequests_return200_emptyList() throws Exception {
        when(itemRequestService.getUserRequests(anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/requests")
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    void getUserRequests_return404_userNotFound() throws Exception {
        when(itemRequestService.getUserRequests(anyLong()))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/requests")
                        .header(USER_HEADER, 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Пользователь не найден")));
    }

    @Test
    void getAllRequests_return200() throws Exception {
        when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/requests/all")
                        .header(USER_HEADER, 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void getAllRequests_return404_userNotFound() throws Exception {
        when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/requests/all")
                        .header(USER_HEADER, 999L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Пользователь не найден")));
    }

    @Test
    void getRequestById_return200() throws Exception {
        when(itemRequestService.getRequestById(anyLong(), anyLong())).thenReturn(responseDto);

        mockMvc.perform(get("/requests/1")
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Нужна дрель")));
    }

    @Test
    void getRequestById_return404_requestNotFound() throws Exception {
        when(itemRequestService.getRequestById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Запрос не найден"));

        mockMvc.perform(get("/requests/999")
                        .header(USER_HEADER, 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Запрос не найден")));
    }

    @Test
    void getRequestById_return404_userNotFound() throws Exception {
        when(itemRequestService.getRequestById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/requests/1")
                        .header(USER_HEADER, 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Пользователь не найден")));
    }
}
