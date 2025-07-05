package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserRequestDTO> requestTester;

    @Autowired
    private JacksonTester<UserResponseDTO> responseTester;

    @Test
    void serialize_ok() throws Exception {
        assertThat(requestTester.write(new UserRequestDTO("Георгий", "user@email.com"))).isNotNull();
        assertThat(responseTester.write(UserResponseDTO.builder().id(1L).build())).isNotNull();
    }
}
