package ru.practicum.shareit.user;

public class UserMapper {
    public static UserDTO toUserDto(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail());
    }

    public static User toUser(UserDTO userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail());
    }
}
