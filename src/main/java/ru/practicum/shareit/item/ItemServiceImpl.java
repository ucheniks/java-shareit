package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingDateDTO;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemResponseDTO createItem(Long ownerId, ItemRequestDTO itemDTO) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + ownerId + " не найден"));

        Item item = ItemMapper.toItem(itemDTO, owner);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemResponseDTO(savedItem);
    }

    @Override
    @Transactional
    public ItemResponseDTO updateItem(Long itemId, Long ownerId, ItemUpdateDTO updateItem) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: " + itemId));

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }

        User owner = existingItem.getOwner();
        Item newItem = ItemMapper.toItemFromUpdate(updateItem, owner, itemId);
        newItem = updateIfNotNull(newItem, existingItem);
        Item updatedItem = itemRepository.save(newItem);
        return ItemMapper.toItemResponseDTO(updatedItem);
    }


    @Override
    public ItemResponseDTO getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .map(ItemMapper::toItemResponseDTO)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: " + itemId));
    }

    @Override
    public List<ItemResponseDTO> getAllItemsByOwner(Long ownerId) {
        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(ownerId);
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();

        Map<Long, Booking> lastBookingsMap = bookingRepository.findLastBookingsForItems(itemIds, now)
                .stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        booking -> booking,
                        (existing, replacement) -> existing
                ));

        Map<Long, Booking> nextBookingsMap = bookingRepository.findNextBookingsForItems(itemIds, now)
                .stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        booking -> booking,
                        (existing, replacement) -> existing
                ));

        return items.stream()
                .map(item -> {
                    ItemResponseDTO dto = ItemMapper.toItemResponseDTO(item);
                    Long itemId = item.getId();

                    Booking lastBooking = lastBookingsMap.get(itemId);
                    if (lastBooking != null) {
                        dto.setLastBooking(
                                BookingDateDTO.builder()
                                        .id(lastBooking.getId())
                                        .bookerId(lastBooking.getBooker().getId())
                                        .build()
                        );
                    }

                    Booking nextBooking = nextBookingsMap.get(itemId);
                    if (nextBooking != null) {
                        dto.setNextBooking(
                                BookingDateDTO.builder()
                                        .id(nextBooking.getId())
                                        .bookerId(nextBooking.getBooker().getId())
                                        .build()
                        );
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDTO> searchAvailableItems(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return itemRepository.searchAvailableItems(text.toLowerCase()).stream()
                .map(ItemMapper::toItemResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Вещь не найдена: " + itemId);
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional
    public CommentResponseDTO addComment(Long userId, Long itemId, String text) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        boolean hasBooking = bookingRepository
                .findByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now())
                .stream()
                .anyMatch(booking -> booking.getStatus() == BookingStatus.APPROVED);

        if (!hasBooking) {
            throw new ValidationException("Вы не можете оставить комментарий, так как не арендовали вещь");
        }

        Comment comment = Comment.builder()
                .text(text)
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        commentRepository.save(comment);
        return CommentMapper.toDTO(comment);
    }

    private Item updateIfNotNull(Item newItem, Item existingItem) {
        if (newItem.getName() != null) {
            existingItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            existingItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            existingItem.setAvailable(newItem.getAvailable());
        }
        return existingItem;
    }
}