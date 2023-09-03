package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingJsonTest {
    @Autowired
    JacksonTester<BookingRequestDto> jacksonTester;

    @Test
    void testBookingResponseDto() throws IOException {
        BookingRequestDto bookingResponseDto = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 11, 23, 15, 20, 13))
                .end(LocalDateTime.of(2023, 11, 29, 15, 20, 13))
                .build();
        JsonContent<BookingRequestDto> jsonContent = jacksonTester.write(bookingResponseDto);
        assertThat(jsonContent).hasJsonPath("$.itemId");
        assertThat(jsonContent).hasJsonPath("$.start");
        assertThat(jsonContent).hasJsonPath("$.end");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.start")
                .isEqualTo(LocalDateTime.of(2023, 11, 23, 15, 20, 13)
                        .toString());
        assertThat(jsonContent).extractingJsonPathStringValue("$.end")
                .isEqualTo(LocalDateTime.of(2023, 11, 29, 15, 20, 13)
                        .toString());
    }
}
