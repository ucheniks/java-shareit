package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingClient bookingClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                                @Valid @RequestBody BookingRequestDTO bookingRequestDTO) {
        log.info("Gateway: создание бронирования: {}, userId={}", bookingRequestDTO, userId);
        return bookingClient.createBooking(bookingRequestDTO, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(USER_ID_HEADER) long userId,
                                                 @PathVariable long bookingId) {
        log.info("Gateway: получение бронирования id={}, userId={}", bookingId, userId);
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookings(@RequestHeader(USER_ID_HEADER) long userId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @RequestParam(required = false) Integer from,
                                                 @RequestParam(required = false) Integer size) {
        log.info("Gateway: получение бронирований пользователя userId={}, state={}, from={}, size={}", userId, state, from, size);
        return bookingClient.getAllBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestHeader(USER_ID_HEADER) long userId,
                                                     @RequestParam(defaultValue = "ALL") String state,
                                                     @RequestParam(required = false) Integer from,
                                                     @RequestParam(required = false) Integer size) {
        log.info("Gateway: получение бронирований владельца userId={}, state={}, from={}, size={}", userId, state, from, size);
        return bookingClient.getBookingsByOwner(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                                 @PathVariable long bookingId,
                                                 @RequestParam boolean approved) {
        log.info("Gateway: одобрение бронирования id={}, approved={}, userId={}", bookingId, approved, userId);
        return bookingClient.approveBooking(bookingId, userId, approved);
    }
}
