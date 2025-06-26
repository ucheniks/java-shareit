package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO userDTO);

    UserResponseDTO updateUser(Long userId, UserUpdateDTO updateUser);

    UserResponseDTO getUserById(Long userId);

    List<UserResponseDTO> getAllUsers();

    void deleteUser(Long userId);

}
