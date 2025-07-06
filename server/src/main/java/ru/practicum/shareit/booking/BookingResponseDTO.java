package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    private Long id;
    private ItemDTO item;
    private BookerDTO booker;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemDTO {
        private Long id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookerDTO {
        private Long id;
    }
}



