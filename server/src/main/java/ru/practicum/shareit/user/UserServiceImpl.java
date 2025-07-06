package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userDTO) {
        validateUserEmail(userDTO.getEmail(), null);

        User user = UserMapper.toUser(userDTO);
        User savedUser = userRepository.save(user);
        return UserMapper.toResponseDto(savedUser);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(Long userId, UserUpdateDTO updateUser) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));

        if (updateUser.getEmail() != null && !updateUser.getEmail().equals(existingUser.getEmail())) {
            validateUserEmail(updateUser.getEmail(), userId);
            existingUser.setEmail(updateUser.getEmail());
        }

        if (updateUser.getName() != null) {
            existingUser.setName(updateUser.getName());
        }

        return UserMapper.toResponseDto(existingUser);
    }

    @Override
    public UserResponseDTO getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::toResponseDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден: " + userId);
        }
        userRepository.deleteById(userId);
    }

    private void validateUserEmail(String email, Long userId) {
        boolean emailExists = userId == null
                ? userRepository.existsByEmail(email)
                : userRepository.existsByEmailAndIdNot(email, userId);

        if (emailExists) {
            throw new ConflictException("Email уже используется: " + email);
        }
    }
}