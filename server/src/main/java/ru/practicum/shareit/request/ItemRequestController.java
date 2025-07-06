package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestResponseDTO createRequest(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestBody ItemRequestReqDTO requestDTO) {
        log.info("Создание запроса пользователем ID: {}", userId);
        return itemRequestService.createRequest(userId, requestDTO);
    }

    @GetMapping
    public List<ItemRequestResponseDTO> getUserRequests(
            @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Получение запросов пользователя ID: {}", userId);
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDTO> getAllRequests(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получение всех запросов (кроме своих) для пользователя ID: {}", userId);
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDTO getRequestById(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long requestId) {
        log.info("Получение запроса ID: {} пользователем ID: {}", requestId, userId);
        return itemRequestService.getRequestById(userId, requestId);
    }
}
