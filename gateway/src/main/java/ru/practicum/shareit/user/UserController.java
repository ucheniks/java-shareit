package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        log.info("Gateway: создание пользователя: {}", userRequestDTO);
        return userClient.createUser(userRequestDTO);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Gateway: получение всех пользователей");
        return userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        log.info("Gateway: получение пользователя по id: {}", userId);
        return userClient.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId,
                                             @RequestBody @Valid UserUpdateDTO updateUserDTO) {
        log.info("Gateway: обновление пользователя id {}: {}", userId, updateUserDTO);
        return userClient.updateUser(userId, updateUserDTO);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) {
        log.info("Gateway: удаление пользователя id {}", userId);
        return userClient.deleteUser(userId);
    }
}
