package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.ItemShortResponseDTO;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestReqDTO> requestTester;

    @Autowired
    private JacksonTester<ItemRequestResponseDTO> responseTester;

    @Autowired
    private ObjectMapper objectMapper;

    private final ItemRequestReqDTO requestDto = new ItemRequestReqDTO("Нужна дрель");
    private final ItemShortResponseDTO itemDto = ItemShortResponseDTO.builder()
            .id(1L)
            .name("Дрель")
            .ownerId(1L)
            .build();
    private final ItemRequestResponseDTO responseDto = ItemRequestResponseDTO.builder()
            .id(1L)
            .description("Нужна дрель")
            .created(LocalDateTime.of(2005, 6, 19, 0, 0))
            .items(Collections.singletonList(itemDto))
            .build();

    @Test
    void reqDto_serialize() throws IOException {
        JsonContent<ItemRequestReqDTO> result = requestTester.write(requestDto);

        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужна дрель");
    }

    @Test
    void reqDto_deserialize() throws IOException {
        String json = "{\"description\":\"Нужен перфоратор\"}";

        ItemRequestReqDTO dto = requestTester.parseObject(json);

        assertThat(dto.getDescription()).isEqualTo("Нужен перфоратор");
    }

    @Test
    void reqDto_noDescription() throws IOException {
        String json = "{}";

        JsonContent<ItemRequestReqDTO> result = requestTester.write(new ItemRequestReqDTO());
        assertThat(result).doesNotHaveJsonPathValue("$.description");

        ItemRequestReqDTO dto = requestTester.parseObject(json);
        assertThat(dto.getDescription()).isNull();
    }

    @Test
    void respDto_serialize() throws IOException {
        JsonContent<ItemRequestResponseDTO> result = responseTester.write(responseDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);

        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужна дрель");

        assertThat(result).hasJsonPathStringValue("$.created");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2005-06-19T00:00:00");

        assertThat(result).hasJsonPathArrayValue("$.items");
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);

        assertThat(result).hasJsonPathStringValue("$.items[0].name");
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Дрель");
    }

    @Test
    void respDto_deserialize() throws IOException {
        String json = "{\"id\":1,\"description\":\"Нужен лазерный уровень\",\"created\":\"2005-06-19T10:30:00\",\"items\":[{\"id\":2,\"name\":\"Уровень\",\"ownerId\":2}]}";

        ItemRequestResponseDTO dto = responseTester.parseObject(json);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Нужен лазерный уровень");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2005, 6, 19, 10, 30));
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().get(0).getName()).isEqualTo("Уровень");
    }

    @Test
    void respDto_emptyItems() throws IOException {
        String json = "{\"id\":1,\"description\":\"Пустой запрос\",\"created\":\"2005-06-19T12:00:00\",\"items\":[]}";

        ItemRequestResponseDTO dto = responseTester.parseObject(json);

        assertThat(dto.getItems()).isEmpty();
    }

    @Test
    void respDto_nullItems() throws IOException {
        String json = "{\"id\":1,\"description\":\"Нет вещей\",\"created\":\"2005-06-19T15:00:00\"}";

        ItemRequestResponseDTO dto = responseTester.parseObject(json);

        assertThat(dto.getItems()).isNull();
    }

    @Test
    void respDto_dateFormat() throws IOException {
        JsonContent<ItemRequestResponseDTO> result = responseTester.write(responseDto);
        String jsonString = result.getJson();

        assertThat(jsonString).contains("\"created\":\"2005-06-19T00:00:00\"");
    }
}
