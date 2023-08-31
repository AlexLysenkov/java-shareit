package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemJsonTest {
    @Autowired
    JacksonTester<ItemResponseDto> jacksonTester;

    @Test
    void testItemResponseDto() throws IOException {
        ItemResponseDto itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .requestId(3L)
                .available(true)
                .build();
        JsonContent<ItemResponseDto> jsonContent = jacksonTester.write(itemResponseDto);
        assertThat(jsonContent).hasJsonPath("$.id");
        assertThat(jsonContent).hasJsonPath("$.name");
        assertThat(jsonContent).hasJsonPath("$.description");
        assertThat(jsonContent).hasJsonPath("$.available");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("Item");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description")
                .isEqualTo("Description");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available").isTrue();
    }
}
