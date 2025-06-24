package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDTO getUserById(@PathVariable Long userId) {
        log.info("Получение пользователя по Id {}", userId);
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<UserDTO> getAll() {
        log.info("Получение всех пользователей");
        return userService.getAllUsers();
    }

    @PostMapping
    public UserDTO createUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("Создание пользователя: {}", userDTO);
        return userService.createUser(userDTO);
    }

    @PatchMapping("/{userId}")
    public UserDTO updateUser(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> updates) {
        log.info("Частичное обновление пользователя ID {}: {}", userId, updates);
        return userService.updateUser(userId, updates);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Удаление пользователя ID {}", userId);
        userService.deleteUser(userId);
    }
}
