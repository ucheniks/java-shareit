package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Captor
    private ArgumentCaptor<UserRequestDTO> requestCaptor;

    @Captor
    private ArgumentCaptor<UserUpdateDTO> updateCaptor;

    private final UserRequestDTO validRequest = new UserRequestDTO("Георгий", "user@email.com");
    private final UserResponseDTO responseDto = UserResponseDTO.builder()
            .id(1L)
            .name("Георгий")
            .email("user@email.com")
            .build();

    @Test
    void createUser_ok() throws Exception {
        when(userService.createUser(any())).thenReturn(responseDto);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(validRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Георгий")))
                .andExpect(jsonPath("$.email", is("user@email.com")));

        verify(userService).createUser(requestCaptor.capture());
        assertEquals("Георгий", requestCaptor.getValue().getName());
    }

    @Test
    void getUserById_ok() throws Exception {
        when(userService.getUserById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Георгий")));
    }

    @Test
    void getUserById_notFound() throws Exception {
        when(userService.getUserById(99L)).thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User not found")));
    }

    @Test
    void getAllUsers_ok() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", is("user@email.com")));
    }

    @Test
    void updateUser_ok() throws Exception {
        UserUpdateDTO updateDto = new UserUpdateDTO();
        updateDto.setName("Георгий Updated");
        UserResponseDTO updatedResponse = UserResponseDTO.builder()
                .id(1L)
                .name("Георгий Updated")
                .email("user@email.com")
                .build();

        when(userService.updateUser(eq(1L), any())).thenReturn(updatedResponse);

        mockMvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Георгий Updated")));

        verify(userService).updateUser(eq(1L), updateCaptor.capture());
        assertEquals("Георгий Updated", updateCaptor.getValue().getName());
    }

    @Test
    void updateUser_conflict() throws Exception {
        UserUpdateDTO updateDto = new UserUpdateDTO();
        updateDto.setEmail("conflict@email.com");

        when(userService.updateUser(eq(1L), any()))
                .thenThrow(new ConflictException("Email already exists"));

        mockMvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("Email already exists")));
    }

    @Test
    void deleteUser_ok() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    void deleteUser_notFound() throws Exception {
        doThrow(new NotFoundException("User not found"))
                .when(userService).deleteUser(99L);

        mockMvc.perform(delete("/users/99"))
                .andExpect(status().isNotFound());
    }
}
