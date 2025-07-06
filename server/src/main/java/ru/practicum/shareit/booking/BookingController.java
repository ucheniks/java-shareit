package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingResponseDTO createBooking(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestBody BookingRequestDTO bookingRequestDto) {
        log.info("Создание бронирования {} пользователем {}", bookingRequestDto, userId);
        return bookingService.createBooking(userId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDTO approveBooking(
            @RequestHeader(USER_ID_HEADER) Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        log.info("Подтверждение бронирования ID {} пользователем {} с approved={}", bookingId, ownerId, approved);
        return bookingService.approveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDTO getBookingById(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long bookingId) {
        log.info("Получение бронирования ID {} пользователем {}", bookingId, userId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDTO> getBookingsByUser(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получение списка бронирований пользователя {} с state={}", userId, state);
        BookingState bookingState = BookingState.from(state);
        return bookingService.getBookingsByUser(userId, bookingState);
    }

    @GetMapping("/owner")
    public List<BookingResponseDTO> getBookingsByOwner(
            @RequestHeader(USER_ID_HEADER) Long ownerId,
            @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получение списка бронирований для вещей владельца {} с state={}", ownerId, state);
        BookingState bookingState = BookingState.from(state);
        return bookingService.getBookingsByOwner(ownerId, bookingState);
    }
}
