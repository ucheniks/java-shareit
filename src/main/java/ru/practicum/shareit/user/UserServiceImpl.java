package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserResponseDTO createUser(UserRequestDTO userCreateDto) {
        User user = UserMapper.toUser(userCreateDto);
        User savedUser = userRepository.addUser(user);
        return UserMapper.toResponseDto(savedUser);
    }

    @Override
    public UserResponseDTO updateUser(Long userId, UserUpdateDTO updateUser) {
        User updatedUser = userRepository.updateUser(userId, updateUser);
        return UserMapper.toResponseDto(updatedUser);
    }

    @Override
    public UserResponseDTO getUserById(Long userId) {
        User user = userRepository.getUserById(userId);
        return UserMapper.toResponseDto(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUserById(userId);
    }

}