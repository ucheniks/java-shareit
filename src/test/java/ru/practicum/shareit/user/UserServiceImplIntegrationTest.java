package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
class UserServiceImplIntegrationTest {

    private final EntityManager em;
    private final UserService userService;

    @Test
    void createUser_savesUser() {
        UserRequestDTO request = new UserRequestDTO("Георгий", "user@email.com");
        UserResponseDTO response = userService.createUser(request);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User saved = query.setParameter("email", "user@email.com").getSingleResult();

        assertThat(saved.getId(), notNullValue());
        assertThat(saved.getName(), equalTo("Георгий"));
        assertThat(saved.getEmail(), equalTo("user@email.com"));
        assertThat(response.getId(), equalTo(saved.getId()));
    }

    @Test
    void createUser_existingEmail_throwsConflict() {
        User existing = User.builder()
                .name("Георгий")
                .email("user@email.com")
                .build();
        em.persist(existing);
        em.flush();

        UserRequestDTO request = new UserRequestDTO("Другой", "user@email.com");

        assertThrows(ConflictException.class, () -> userService.createUser(request));
    }

    @Test
    void updateUser_updatesData() {
        User user = User.builder()
                .name("Георгий")
                .email("old@email.com")
                .build();
        em.persist(user);
        em.flush();

        UserUpdateDTO update = new UserUpdateDTO();
        update.setName("Новый Георгий");
        update.setEmail("new@email.com");
        userService.updateUser(user.getId(), update);

        User updated = em.find(User.class, user.getId());
        assertThat(updated.getName(), equalTo("Новый Георгий"));
        assertThat(updated.getEmail(), equalTo("new@email.com"));
    }

    @Test
    void getUserById_returnsUser() {
        User user = User.builder()
                .name("Георгий")
                .email("user@email.com")
                .build();
        em.persist(user);
        em.flush();

        UserResponseDTO result = userService.getUserById(user.getId());

        assertThat(result.getId(), equalTo(user.getId()));
        assertThat(result.getName(), equalTo("Георгий"));
        assertThat(result.getEmail(), equalTo("user@email.com"));
    }

    @Test
    void getAllUsers_returnsAll() {
        User user1 = User.builder().name("Георгий1").email("user1@email.com").build();
        User user2 = User.builder().name("Георгий2").email("user2@email.com").build();
        em.persist(user1);
        em.persist(user2);
        em.flush();

        List<UserResponseDTO> result = userService.getAllUsers();

        assertThat(result, hasSize(2));
        assertThat(result, hasItem(allOf(
                hasProperty("id", equalTo(user1.getId())),
                hasProperty("name", equalTo("Георгий1")),
                hasProperty("email", equalTo("user1@email.com"))
        )));
        assertThat(result, hasItem(allOf(
                hasProperty("id", equalTo(user2.getId())),
                hasProperty("name", equalTo("Георгий2")),
                hasProperty("email", equalTo("user2@email.com"))
        )));
    }

    @Test
    void deleteUser_removesUser() {
        User user = User.builder()
                .name("Георгий")
                .email("delete@email.com")
                .build();
        em.persist(user);
        em.flush();

        userService.deleteUser(user.getId());

        User deleted = em.find(User.class, user.getId());
        assertThat(deleted, nullValue());
    }
}
