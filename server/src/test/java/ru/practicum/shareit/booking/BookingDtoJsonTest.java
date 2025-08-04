package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingRequestDTO> requestTester;
    @Autowired
    private JacksonTester<BookingResponseDTO> responseTester;
    @Autowired
    private JacksonTester<BookingDateDTO> dateTester;
    @Autowired
    private ObjectMapper objectMapper;

    private final LocalDateTime start = LocalDateTime.of(2005, 6, 19, 0, 0);
    private final LocalDateTime end = LocalDateTime.of(2005, 6, 20, 0, 0);

    @Test
    @SneakyThrows
    void bookingRequestDto_serialization() {
        BookingRequestDTO dto = new BookingRequestDTO(1L, start, end);

        JsonContent<BookingRequestDTO> result = requestTester.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2005-06-19T00:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2005-06-20T00:00:00");
    }

    @Test
    @SneakyThrows
    void bookingRequestDto_deserialization() {
        String json = "{\"itemId\": 1, \"start\": \"2005-06-19T00:00:00\", \"end\": \"2005-06-20T00:00:00\"}";

        BookingRequestDTO dto = requestTester.parseObject(json);

        assertThat(dto.getItemId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(start);
        assertThat(dto.getEnd()).isEqualTo(end);
    }

    @Test
    @SneakyThrows
    void bookingResponseDto_serialization() {
        BookingResponseDTO dto = BookingResponseDTO.builder()
                .id(1L)
                .item(new BookingResponseDTO.ItemDTO(2L, "Дрель"))
                .booker(new BookingResponseDTO.BookerDTO(3L))
                .start(start)
                .end(end)
                .status(BookingStatus.WAITING)
                .build();

        JsonContent<BookingResponseDTO> result = responseTester.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2005-06-19T00:00:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }

    @Test
    @SneakyThrows
    void bookingResponseDto_deserialization() {
        String json = "{\"id\":1,\"start\":\"2005-06-19T00:00:00\",\"end\":\"2005-06-20T00:00:00\"," +
                "\"status\":\"APPROVED\",\"item\":{\"id\":2,\"name\":\"Дрель\"}," +
                "\"booker\":{\"id\":3}}";

        BookingResponseDTO dto = responseTester.parseObject(json);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getItem().getId()).isEqualTo(2L);
        assertThat(dto.getItem().getName()).isEqualTo("Дрель");
        assertThat(dto.getBooker().getId()).isEqualTo(3L);
        assertThat(dto.getStart()).isEqualTo(start);
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    @SneakyThrows
    void bookingDateDto_serialization() {
        BookingDateDTO dto = new BookingDateDTO(1L, 2L);

        JsonContent<BookingDateDTO> result = dateTester.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
    }

    @Test
    @SneakyThrows
    void bookingDateDto_deserialization() {
        String json = "{\"id\": 1, \"bookerId\": 2}";

        BookingDateDTO dto = dateTester.parseObject(json);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getBookerId()).isEqualTo(2L);
    }

    @Test
    void shouldSerializeDateTimeInIsoFormat() throws JsonProcessingException {
        BookingRequestDTO dto = new BookingRequestDTO(1L, start, end);
        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"start\":\"2005-06-19T00:00:00\"");
        assertThat(json).contains("\"end\":\"2005-06-20T00:00:00\"");
    }

    @Test
    void shouldSerializeBookingStatus() throws JsonProcessingException {
        BookingResponseDTO dto = BookingResponseDTO.builder()
                .status(BookingStatus.REJECTED)
                .build();

        String json = objectMapper.writeValueAsString(dto);
        assertThat(json).contains("\"status\":\"REJECTED\"");
    }

    @Test
    void shouldDeserializeBookingStatus() throws JsonProcessingException {
        String json = "{\"status\":\"CANCELED\"}";
        BookingResponseDTO dto = objectMapper.readValue(json, BookingResponseDTO.class);

        assertThat(dto.getStatus()).isEqualTo(BookingStatus.CANCELED);
    }
}