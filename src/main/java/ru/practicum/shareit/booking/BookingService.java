package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {
    private static final int PAGE_SIZE = 10;

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingResponseDTO createBooking(Long userId, BookingRequestDTO bookingRequestDTO) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(bookingRequestDTO.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new ConflictException("Владелец не может бронировать свою вещь");
        }

        Booking booking = BookingMapper.toBooking(bookingRequestDTO, item, booker);
        Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.toBookingResponseDTO(savedBooking);
    }

    public BookingResponseDTO approveBooking(Long ownerId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ValidationException("Только владелец может подтверждать бронирование");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ConflictException("Бронирование уже подтверждено или отклонено");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingResponseDTO(booking);
    }

    public BookingResponseDTO getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ConflictException("Нет доступа к этому бронированию");
        }

        return BookingMapper.toBookingResponseDTO(booking);
    }

    public List<BookingResponseDTO> getBookingsByUser(Long userId, BookingState bookingState) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Pageable pageable = PageRequest.of(0, PAGE_SIZE, Sort.by("start").descending());
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings;

        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findByBookerId(userId, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentByBookerId(userId, now, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findPastByBookerId(userId, now, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureByBookerId(userId, now, pageable);
                break;
            case WAITING:
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, bookingState.toStatus(), pageable);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный параметр: " + bookingState);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingResponseDTO)
                .collect(Collectors.toList());
    }

    public List<BookingResponseDTO> getBookingsByOwner(Long ownerId, BookingState bookingState) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Владелец не найден"));

        Pageable pageable = PageRequest.of(0, PAGE_SIZE, Sort.by("start").descending());
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings;

        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findByOwnerId(ownerId, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentByOwnerId(ownerId, now, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findPastByOwnerId(ownerId, now, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureByOwnerId(ownerId, now, pageable);
                break;
            case WAITING:
            case REJECTED:
                bookings = bookingRepository.findByItem_OwnerIdAndStatus(ownerId, bookingState.toStatus(), pageable);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный параметр: " + bookingState);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingResponseDTO)
                .collect(Collectors.toList());
    }
}
