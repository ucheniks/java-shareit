package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private final BookingRequestDTO validRequest = new BookingRequestDTO(
            1L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
    );

    private final BookingResponseDTO responseDto = BookingResponseDTO.builder()
            .id(1L)
            .item(new BookingResponseDTO.ItemDTO(1L, "Дрель"))
            .booker(new BookingResponseDTO.BookerDTO(1L))
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2))
            .status(BookingStatus.WAITING)
            .build();

    @Test
    void createBooking_201() throws Exception {
        when(bookingService.createBooking(anyLong(), any())).thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(validRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("WAITING")))
                .andExpect(jsonPath("$.item.name", is("Дрель")));
    }

    @Test
    void createBooking_400() throws Exception {
        BookingRequestDTO invalidRequest = new BookingRequestDTO(
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        );

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveBooking_200() throws Exception {
        BookingResponseDTO approvedResponse = BookingResponseDTO.builder()
                .id(1L)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(approvedResponse);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void approveBooking_404() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new NotFoundException("Бронирование не найдено"));

        mockMvc.perform(patch("/bookings/999?approved=true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("Бронирование не найдено")));
    }

    @Test
    void getBookingById_200() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(responseDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void getBookingById_403() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenThrow(new ConflictException("Нет доступа"));

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 999L))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", containsString("Нет доступа")));
    }

    @Test
    void getBookingsByUser_200() throws Exception {
        when(bookingService.getBookingsByUser(anyLong(), any()))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].item.name", is("Дрель")));
    }

    @Test
    void getBookingsByUser_400() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "INVALID"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", containsString("Неизвестный параметр")));
    }

    @Test
    void getBookingsByOwner_200() throws Exception {
        when(bookingService.getBookingsByOwner(anyLong(), any()))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getBookingsByOwner_EmptyList() throws Exception {
        when(bookingService.getBookingsByOwner(anyLong(), eq(BookingState.REJECTED)))
                .thenReturn(List.of());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "REJECTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }
}