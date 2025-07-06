package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void createItem_shouldSaveItem() {
        User owner = userRepository.save(new User(null, "Георгий", "owner@email.com"));
        ItemRequestDTO request = new ItemRequestDTO("Дрель", "Простая дрель", true, null);

        ItemResponseDTO response = itemService.createItem(owner.getId(), request);

        Item savedItem = itemRepository.findById(response.getId()).orElseThrow();
        assertThat(savedItem.getName(), equalTo("Дрель"));
        assertThat(savedItem.getOwner().getId(), equalTo(owner.getId()));
    }

    @Test
    void createItem_withRequest_shouldLinkToRequest() {
        User owner = userRepository.save(new User(null, "Георгий", "owner@email.com"));
        User requestor = userRepository.save(new User(null, "Георгий", "requestor@email.com"));
        ItemRequest request = itemRequestRepository.save(
                new ItemRequest(null, "Нужна дрель", requestor, LocalDateTime.of(2005, 6, 19, 0, 0), new ArrayList<>())
        );

        ItemRequestDTO itemDto = new ItemRequestDTO("Дрель", "Простая дрель", true, request.getId());
        ItemResponseDTO response = itemService.createItem(owner.getId(), itemDto);

        Item savedItem = itemRepository.findById(response.getId()).orElseThrow();
        assertThat(savedItem.getRequest().getId(), equalTo(request.getId()));
    }

    @Test
    void updateItem_shouldUpdate() {
        User owner = userRepository.save(new User(null, "Георгий", "owner@email.com"));
        Item item = itemRepository.save(
                Item.builder()
                        .name("Старая дрель")
                        .description("Очень старая")
                        .available(true)
                        .owner(owner)
                        .build()
        );

        ItemUpdateDTO update = new ItemUpdateDTO();
        update.setName("Дрель");
        update.setDescription("Современная дрель");
        itemService.updateItem(item.getId(), owner.getId(), update);

        Item updatedItem = itemRepository.findById(item.getId()).orElseThrow();
        assertThat(updatedItem.getName(), equalTo("Дрель"));
        assertThat(updatedItem.getDescription(), equalTo("Современная дрель"));
    }

    @Test
    void updateItem_byNonOwner_shouldThrow() {
        User owner = userRepository.save(new User(null, "Георгий1", "owner@email.com"));
        User nonOwner = userRepository.save(new User(null, "Георгий2", "nonowner@email.com"));
        Item item = itemRepository.save(
                Item.builder()
                        .name("Дрель")
                        .description("Простая")
                        .available(true)
                        .owner(owner)
                        .build()
        );

        ItemUpdateDTO update = new ItemUpdateDTO();
        update.setName("Новая дрель");

        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(item.getId(), nonOwner.getId(), update));
    }

    @Test
    void getItemById_shouldReturn() {
        User owner = userRepository.save(new User(null, "Георгий", "owner@email.com"));
        Item item = itemRepository.save(
                Item.builder()
                        .name("Дрель")
                        .description("Простая")
                        .available(true)
                        .owner(owner)
                        .build()
        );

        ItemResponseDTO result = itemService.getItemById(item.getId());

        assertThat(result.getId(), equalTo(item.getId()));
        assertThat(result.getName(), equalTo("Дрель"));
    }

    @Test
    void getAllItemsByOwner_shouldReturnWithBookings() {
        User owner = userRepository.save(new User(null, "Георгий1", "owner@email.com"));
        User booker = userRepository.save(new User(null, "Георгий2", "booker@email.com"));
        Item item = itemRepository.save(
                Item.builder()
                        .name("Дрель")
                        .description("Простая")
                        .available(true)
                        .owner(owner)
                        .build()
        );

        LocalDateTime now = LocalDateTime.now();

        bookingRepository.save(
                Booking.builder()
                        .start(now.minusDays(2))
                        .end(now.minusDays(1))
                        .item(item)
                        .booker(booker)
                        .status(BookingStatus.APPROVED)
                        .build()
        );

        bookingRepository.save(
                Booking.builder()
                        .start(now.plusDays(1))
                        .end(now.plusDays(2))
                        .item(item)
                        .booker(booker)
                        .status(BookingStatus.APPROVED)
                        .build()
        );

        List<ItemResponseDTO> items = itemService.getAllItemsByOwner(owner.getId());

        assertThat(items, hasSize(1));
        assertThat(items.get(0).getLastBooking(), notNullValue());
        assertThat(items.get(0).getNextBooking(), notNullValue());
    }

    @Test
    void searchAvailableItems_shouldReturn() {
        User owner = userRepository.save(new User(null, "Георгий", "owner@email.com"));
        itemRepository.save(
                Item.builder()
                        .name("Дрель")
                        .description("Аккумуляторная дрель")
                        .available(true)
                        .owner(owner)
                        .build()
        );
        itemRepository.save(
                Item.builder()
                        .name("Отвертка")
                        .description("Крестовая")
                        .available(true)
                        .owner(owner)
                        .build()
        );

        List<ItemResponseDTO> foundItems = itemService.searchAvailableItems("дрель");

        assertThat(foundItems, hasSize(1));
        assertThat(foundItems.get(0).getName(), equalTo("Дрель"));
    }

    @Test
    void addComment_shouldSave() {
        User owner = userRepository.save(new User(null, "Георгий", "owner@email.com"));
        User booker = userRepository.save(new User(null, "Георгий", "booker@email.com"));
        Item item = itemRepository.save(
                Item.builder()
                        .name("Дрель")
                        .description("Простая")
                        .available(true)
                        .owner(owner)
                        .build()
        );

        bookingRepository.save(
                Booking.builder()
                        .start(LocalDateTime.of(2005, 6, 17, 0, 0))
                        .end(LocalDateTime.of(2005, 6, 18, 0, 0))
                        .item(item)
                        .booker(booker)
                        .status(BookingStatus.APPROVED)
                        .build()
        );

        CommentResponseDTO comment = itemService.addComment(booker.getId(), item.getId(), "Хорошая дрель!");

        assertThat(comment.getText(), equalTo("Хорошая дрель!"));
        assertThat(commentRepository.findById(comment.getId()).isPresent(), is(true));
    }

    @Test
    void addComment_withoutBooking_shouldThrow() {
        User owner = userRepository.save(new User(null, "Георгий", "owner@email.com"));
        User nonBooker = userRepository.save(new User(null, "Георгий", "nonbooker@email.com"));
        Item item = itemRepository.save(
                Item.builder()
                        .name("Дрель")
                        .description("Простая")
                        .available(true)
                        .owner(owner)
                        .build()
        );

        assertThrows(ValidationException.class,
                () -> itemService.addComment(nonBooker.getId(), item.getId(), "Не должен добавиться"));
    }
}
