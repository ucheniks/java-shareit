package ru.practicum.shareit.user;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {
    public User toUser(UserRequestDTO dto) {
        return new User(null, dto.getName(), dto.getEmail());
    }

    public UserResponseDTO toResponseDto(User user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail());
    }
}