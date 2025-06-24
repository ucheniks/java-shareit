package ru.practicum.shareit.user;

import java.util.List;
import java.util.Map;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);

    UserDTO updateUser(Long userId, Map<String, Object> updates);

    UserDTO getUserById(Long userId);

    List<UserDTO> getAllUsers();

    void deleteUser(Long userId);

    boolean existsById(Long userId);
}
