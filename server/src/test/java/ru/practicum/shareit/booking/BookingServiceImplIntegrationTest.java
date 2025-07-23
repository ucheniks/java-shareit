package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private User booker;
    private Item availableItem;
    private Item unavailableItem;
    private Booking futureBooking;
    private Booking pastBooking;

    @BeforeEach
    void setup() {
        owner = userRepository.save(new User(null, "Георгий", "owner@email.com"));
        booker = userRepository.save(new User(null, "Георгий", "booker@email.com"));

        availableItem = itemRepository.save(
                Item.builder()
                        .name("Дрель")
                        .description("Аккумуляторная")
                        .available(true)
                        .owner(owner)
                        .build()
        );

        unavailableItem = itemRepository.save(
                Item.builder()
                        .name("Молоток")
                        .description("Сломанный")
                        .available(false)
                        .owner(owner)
                        .build()
        );

        futureBooking = bookingRepository.save(
                Booking.builder()
                        .start(LocalDateTime.now().plusDays(1))
                        .end(LocalDateTime.now().plusDays(2))
                        .item(availableItem)
                        .booker(booker)
                        .status(BookingStatus.WAITING)
                        .build()
        );

        pastBooking = bookingRepository.save(
                Booking.builder()
                        .start(LocalDateTime.now().minusDays(2))
                        .end(LocalDateTime.now().minusDays(1))
                        .item(availableItem)
                        .booker(booker)
                        .status(BookingStatus.APPROVED)
                        .build()
        );
    }

    @Test
    void create_whenAvailable() {
        BookingRequestDTO request = new BookingRequestDTO(
                availableItem.getId(),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        BookingResponseDTO response = bookingService.createBooking(booker.getId(), request);

        assertThat(response.getId(), notNullValue());
        assertThat(response.getItem().getId(), equalTo(availableItem.getId()));
        assertThat(response.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void create_whenOwnerBookingOwnItem() {
        BookingRequestDTO request = new BookingRequestDTO(
                availableItem.getId(),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        assertThrows(ConflictException.class,
                () -> bookingService.createBooking(owner.getId(), request));
    }

    @Test
    void create_whenUnavailable() {
        BookingRequestDTO request = new BookingRequestDTO(
                unavailableItem.getId(),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(booker.getId(), request));
    }

    @Test
    void approve_whenOwnerApproves() {
        BookingResponseDTO response = bookingService.approveBooking(
                owner.getId(),
                futureBooking.getId(),
                true
        );

        assertThat(response.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void approve_whenOwnerRejects() {
        BookingResponseDTO response = bookingService.approveBooking(
                owner.getId(),
                futureBooking.getId(),
                false
        );

        assertThat(response.getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void approve_whenNotOwner() {
        assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(booker.getId(), futureBooking.getId(), true));
    }

    @Test
    void approve_whenNotWaiting() {
        assertThrows(ConflictException.class,
                () -> bookingService.approveBooking(owner.getId(), pastBooking.getId(), true));
    }

    @Test
    void getById_owner() {
        BookingResponseDTO response = bookingService.getBookingById(owner.getId(), pastBooking.getId());

        assertThat(response.getId(), equalTo(pastBooking.getId()));
        assertThat(response.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void getById_booker() {
        BookingResponseDTO response = bookingService.getBookingById(booker.getId(), pastBooking.getId());

        assertThat(response.getId(), equalTo(pastBooking.getId()));
    }

    @Test
    void getById_unauthorized() {
        User stranger = userRepository.save(new User(null, "Георгий", "stranger@email.com"));

        assertThrows(ConflictException.class,
                () -> bookingService.getBookingById(stranger.getId(), pastBooking.getId()));
    }

    @Test
    void getByUser_all() {
        List<BookingResponseDTO> bookings = bookingService.getBookingsByUser(
                booker.getId(),
                BookingState.ALL
        );

        assertThat(bookings, hasSize(2));
        assertThat(bookings.get(0).getId(), equalTo(futureBooking.getId()));
    }

    @Test
    void getByUser_current() {
        Booking currentBooking = bookingRepository.save(
                Booking.builder()
                        .start(LocalDateTime.now().minusHours(1))
                        .end(LocalDateTime.now().plusHours(1))
                        .item(availableItem)
                        .booker(booker)
                        .status(BookingStatus.APPROVED)
                        .build()
        );

        List<BookingResponseDTO> bookings = bookingService.getBookingsByUser(
                booker.getId(),
                BookingState.CURRENT
        );

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), equalTo(currentBooking.getId()));
    }

    @Test
    void getByUser_past() {
        List<BookingResponseDTO> bookings = bookingService.getBookingsByUser(
                booker.getId(),
                BookingState.PAST
        );

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), equalTo(pastBooking.getId()));
    }

    @Test
    void getByUser_future() {
        List<BookingResponseDTO> bookings = bookingService.getBookingsByUser(
                booker.getId(),
                BookingState.FUTURE
        );

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), equalTo(futureBooking.getId()));
    }

    @Test
    void getByOwner_all() {
        List<BookingResponseDTO> bookings = bookingService.getBookingsByOwner(
                owner.getId(),
                BookingState.ALL
        );

        assertThat(bookings, hasSize(2));
    }

    @Test
    void getByOwner_waiting() {
        List<BookingResponseDTO> bookings = bookingService.getBookingsByOwner(
                owner.getId(),
                BookingState.WAITING
        );

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getStatus(), equalTo(BookingStatus.WAITING));
    }
}
