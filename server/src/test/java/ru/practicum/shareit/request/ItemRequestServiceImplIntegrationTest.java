package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void createRequest_save() {
        User user = userRepository.save(new User(null, "Георгий", "user@email.com"));
        ItemRequestReqDTO dto = new ItemRequestReqDTO("Нужна дрель");

        var response = itemRequestService.createRequest(user.getId(), dto);

        assertThat(response.getId(), notNullValue());
        assertThat(response.getDescription(), equalTo("Нужна дрель"));

        var saved = itemRequestRepository.findById(response.getId()).orElseThrow();
        assertThat(saved.getDescription(), equalTo("Нужна дрель"));
        assertThat(saved.getRequestor().getId(), equalTo(user.getId()));
    }

    @Test
    void createRequest_userNotFound() {
        ItemRequestReqDTO dto = new ItemRequestReqDTO("Нужна дрель");

        assertThrows(NotFoundException.class,
                () -> itemRequestService.createRequest(999L, dto));
    }

    @Test
    void getUserRequests_onlyOwn() {
        User u1 = userRepository.save(new User(null, "Георгий1", "user1@email.com"));
        User u2 = userRepository.save(new User(null, "Георгий2", "user2@email.com"));

        createRequest(u1, "Запрос 1");
        createRequest(u2, "Запрос 2");

        List<ItemRequestResponseDTO> requests = itemRequestService.getUserRequests(u1.getId());

        assertThat(requests, hasSize(1));
        assertThat(requests.get(0).getDescription(), equalTo("Запрос 1"));
    }

    @Test
    void getUserRequests_empty() {
        User user = userRepository.save(new User(null, "Георгий", "user@email.com"));

        List<ItemRequestResponseDTO> requests = itemRequestService.getUserRequests(user.getId());

        assertThat(requests, empty());
    }

    @Test
    void getAllRequests_otherUsers() {
        User u1 = userRepository.save(new User(null, "Георгий1", "user1@email.com"));
        User u2 = userRepository.save(new User(null, "Георгий2", "user2@email.com"));
        User u3 = userRepository.save(new User(null, "Георгий3", "user3@email.com"));

        createRequest(u1, "Запрос 1");
        createRequest(u2, "Запрос 2");
        createRequest(u3, "Запрос 3");

        List<ItemRequestResponseDTO> requests = itemRequestService.getAllRequests(u1.getId(), 0, 10);

        assertThat(requests, hasSize(2));
        assertThat(requests, everyItem(hasProperty("description", containsString("Запрос"))));
    }


    @Test
    void getRequestById_notFound() {
        User user = userRepository.save(new User(null, "Георгий", "user@email.com"));

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(user.getId(), 999L));
    }

    @Test
    void getRequestById_otherUser() {
        User reqUser = userRepository.save(new User(null, "Георгий1", "requestor@email.com"));
        User otherUser = userRepository.save(new User(null, "Георгий2", "other@email.com"));

        var request = createRequest(reqUser, "Нужна дрель");

        var response = itemRequestService.getRequestById(otherUser.getId(), request.getId());

        assertThat(response.getId(), equalTo(request.getId()));
    }

    private ItemRequest createRequest(User user, String description) {
        ItemRequest request = ItemRequest.builder()
                .description(description)
                .requestor(user)
                .created(LocalDateTime.of(2005, 6, 19, 0, 0))
                .items(new ArrayList<>())
                .build();
        return itemRequestRepository.save(request);
    }
}
