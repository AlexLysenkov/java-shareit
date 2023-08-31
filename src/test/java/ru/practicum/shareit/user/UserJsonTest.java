package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserJsonTest {
    @Autowired
    private JacksonTester<UserDto> jacksonTester;

    @Test
    void testUserDto() throws IOException {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Name")
                .email("some@email.ru")
                .build();
        JsonContent<UserDto> dtoJsonContent = jacksonTester.write(userDto);
        assertThat(dtoJsonContent).hasJsonPath("$.id");
        assertThat(dtoJsonContent).hasJsonPath("$.name");
        assertThat(dtoJsonContent).hasJsonPath("$.email");
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.name").isEqualTo("Name");
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.email")
                .isEqualTo("some@email.ru");
    }
}
