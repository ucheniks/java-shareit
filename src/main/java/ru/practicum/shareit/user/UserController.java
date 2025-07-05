package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public UserResponseDTO getUserById(@PathVariable Long userId) {
        log.info("Получение пользователя по Id {}", userId);
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<UserResponseDTO> getAll() {
        log.info("Получение всех пользователей");
        return userService.getAllUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO createUser(@Valid @RequestBody UserRequestDTO userDTO) {
        log.info("Создание пользователя: {}", userDTO);
        return userService.createUser(userDTO);
    }

    @PatchMapping("/{userId}")
    public UserResponseDTO updateUser(
            @PathVariable Long userId,
            @RequestBody @Valid UserUpdateDTO updateUser) {
        log.info("Частичное обновление пользователя ID {}: {}", userId, updateUser);
        return userService.updateUser(userId, updateUser);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("Удаление пользователя ID {}", userId);
        userService.deleteUser(userId);
    }
}
