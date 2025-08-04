package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public ItemRequestResponseDTO createRequest(Long userId, ItemRequestReqDTO requestDTO) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        ItemRequest request = ItemRequest.builder()
                .description(requestDTO.getDescription())
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();

        ItemRequest savedRequest = itemRequestRepository.save(request);
        return ItemRequestMapper.toItemRequestResponseDTO(savedRequest);
    }

    @Override
    public List<ItemRequestResponseDTO> getUserRequests(Long userId) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        return itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId).stream()
                .map(ItemRequestMapper::toItemRequestResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestResponseDTO> getAllRequests(Long userId, Integer from, Integer size) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        if (from < 0) {
            throw new ValidationException("Страница не может быть отрицательной");
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
        return itemRequestRepository.findAllByRequestorIdNot(userId, pageable).stream()
                .map(ItemRequestMapper::toItemRequestResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestResponseDTO getRequestById(Long userId, Long requestId) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с ID " + requestId + " не найден"));

        return ItemRequestMapper.toItemRequestResponseDTO(request);
    }

}
