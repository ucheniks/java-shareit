package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {
    ItemRequestResponseDTO createRequest(Long userId, ItemRequestReqDTO requestDTO);

    List<ItemRequestResponseDTO> getUserRequests(Long userId);

    List<ItemRequestResponseDTO> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequestResponseDTO getRequestById(Long userId, Long requestId);
}
