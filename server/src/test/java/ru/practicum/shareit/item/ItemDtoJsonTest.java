package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingDateDTO;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDTO> requestTester;

    @Autowired
    private JacksonTester<ItemResponseDTO> responseTester;

    @Autowired
    private JacksonTester<ItemUpdateDTO> updateTester;

    @Autowired
    private JacksonTester<CommentRequestDTO> commentRequestTester;

    @Autowired
    private JacksonTester<CommentResponseDTO> commentResponseTester;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void request_serialization() {
        ItemRequestDTO dto = new ItemRequestDTO(
                "Дрель",
                "Аккумуляторная дрель с перфоратором",
                true,
                1L
        );

        JsonContent<ItemRequestDTO> result = requestTester.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Аккумуляторная дрель с перфоратором");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    @Test
    @SneakyThrows
    void request_deserialization() {
        String json = "{\"name\":\"Дрель\",\"description\":\"Простая дрель\",\"available\":true,\"requestId\":1}";

        ItemRequestDTO dto = requestTester.parseObject(json);

        assertThat(dto.getName()).isEqualTo("Дрель");
        assertThat(dto.getDescription()).isEqualTo("Простая дрель");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isEqualTo(1L);
    }

    @Test
    @SneakyThrows
    void response_serialization() {
        BookingDateDTO lastBooking = new BookingDateDTO(1L, 2L);
        BookingDateDTO nextBooking = new BookingDateDTO(3L, 4L);

        CommentResponseDTO comment = CommentResponseDTO.builder()
                .id(1L)
                .text("Отличная дрель!")
                .authorName("Георгий")
                .created(LocalDateTime.of(2005, 6, 19, 12, 0))
                .build();

        ItemResponseDTO dto = ItemResponseDTO.builder()
                .id(1L)
                .name("Дрель")
                .description("Аккумуляторная")
                .available(true)
                .ownerId(1L)
                .requestId(1L)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(List.of(comment))
                .build();

        JsonContent<ItemResponseDTO> result = responseTester.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(4);
        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("Отличная дрель!");
    }

    @Test
    @SneakyThrows
    void response_deserialization() {
        String json = "{\"id\":1,\"name\":\"Дрель\",\"available\":true,\"lastBooking\":{\"id\":1,\"bookerId\":2}}";

        ItemResponseDTO dto = responseTester.parseObject(json);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Дрель");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getLastBooking().getId()).isEqualTo(1L);
        assertThat(dto.getLastBooking().getBookerId()).isEqualTo(2L);
    }

    @Test
    @SneakyThrows
    void update_serialization() {
        ItemUpdateDTO dto = new ItemUpdateDTO();
        dto.setName("Новая дрель");
        dto.setAvailable(false);

        JsonContent<ItemUpdateDTO> result = updateTester.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Новая дрель");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isFalse();
        assertThat(result).doesNotHaveJsonPathValue("$.description");
    }

    @Test
    @SneakyThrows
    void commentRequest_serialization() {
        CommentRequestDTO dto = new CommentRequestDTO("Текст комментария");

        JsonContent<CommentRequestDTO> result = commentRequestTester.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Текст комментария");
    }

    @Test
    @SneakyThrows
    void commentResponse_serialization() throws JsonProcessingException {
        CommentResponseDTO dto = CommentResponseDTO.builder()
                .id(1L)
                .text("Комментарий")
                .authorName("Георгий")
                .created(LocalDateTime.of(2005, 6, 19, 12, 0))
                .build();

        JsonContent<CommentResponseDTO> result = commentResponseTester.write(dto);
        String json = objectMapper.writeValueAsString(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Комментарий");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Георгий");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2005-06-19T12:00:00");
    }
}
