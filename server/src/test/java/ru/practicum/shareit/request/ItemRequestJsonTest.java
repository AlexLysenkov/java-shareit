package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestJsonTest {
    @Autowired
    JacksonTester<ItemRequestDtoResponse> jacksonTester;

    @Test
    void testItemRequestInfoDto() throws IOException {
        ItemRequestDtoResponse itemRequestInfoDto = ItemRequestDtoResponse.builder()
                .id(1L)
                .description("Description")
                .created(LocalDateTime.of(2023, 11, 23, 15, 20, 13))
                .build();
        JsonContent<ItemRequestDtoResponse> jsonContent = jacksonTester.write(itemRequestInfoDto);
        assertThat(jsonContent).hasJsonPath("$.id");
        assertThat(jsonContent).hasJsonPath("$.description");
        assertThat(jsonContent).hasJsonPath("$.created");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.description")
                .isEqualTo("Description");
        assertThat(jsonContent).extractingJsonPathStringValue("$.created")
                .isEqualTo(LocalDateTime.of(2023, 11, 23, 15, 20, 13)
                        .toString());
    }
}
