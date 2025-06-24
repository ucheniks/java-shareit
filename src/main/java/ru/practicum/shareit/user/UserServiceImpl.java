package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDTO createUser(UserDTO userCreateDto) {
        User user = UserMapper.toUser(userCreateDto);
        User savedUser = userRepository.addUser(user);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDTO updateUser(Long userId, Map<String, Object> updates) {
        User updatedUser = userRepository.updateUser(userId, updates);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public UserDTO getUserById(Long userId) {
        User user = userRepository.getUserById(userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUserById(userId);
    }

    @Override
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

}