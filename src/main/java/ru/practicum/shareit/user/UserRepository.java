package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();

    public User addUser(User user) {
        if (emails.contains(user.getEmail())) {
            throw new ConflictException("Email уже используется: " + user.getEmail());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    public User updateUser(Long userId, UserUpdateDTO updateUser) {
        User existingUser = getUserById(userId);

        if (updateUser.getName() != null) {
            existingUser.setName(updateUser.getName());
        }

        if (updateUser.getEmail() != null) {
            String newEmail = updateUser.getEmail();
            if (!newEmail.equals(existingUser.getEmail())) {
                if (emails.contains(newEmail)) {
                    throw new ConflictException("Email уже используется: " + newEmail);
                }
                emails.remove(existingUser.getEmail());
                existingUser.setEmail(newEmail);
                emails.add(newEmail);
            }
        }

        users.put(userId, existingUser);
        return existingUser;
    }

    public User getUserById(Long id) {
        return Optional.ofNullable(users.get(id)).orElseThrow(() -> new NotFoundException("Пользователь не найден: " + id));
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public void deleteUserById(Long id) {
        User user = users.remove(id);
        if (user != null) {
            emails.remove(user.getEmail());
        }
    }

    public boolean existsById(Long id) {
        return users.containsKey(id);
    }


    private Long getNextId() {
        long maxId = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++maxId;
    }
}
