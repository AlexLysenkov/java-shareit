package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utils.Constants.HEADER;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;
    private ItemRequestInfoDto itemRequestInfoDto;
    private ItemRequestDtoResponse itemRequestDtoResponse;

    @BeforeEach
    void setUp() {
        itemRequestDtoResponse = ItemRequestDtoResponse.builder()
                .id(1L)
                .description("Description")
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        itemRequestInfoDto = ItemRequestInfoDto.builder()
                .description("Description")
                .build();
    }

    @Test
    void testCreateItemRequest() throws Exception {
        Mockito
                .when(itemRequestService.createItemRequest(Mockito.any(), Mockito.anyLong()))
                .thenReturn(itemRequestDtoResponse);
        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestInfoDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemRequestDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestInfoDto.getDescription())));
    }

    @Test
    void testGetAllItemRequestsById() throws Exception {
        Mockito
                .when(itemRequestService.getItemRequestsByRequesterId(Mockito.anyLong())).thenReturn(new ArrayList<>());
        mvc.perform((get("/requests"))
                        .header(HEADER, "1"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void testGetAllItemRequests() throws Exception {
        Mockito
                .when(itemRequestService.getAllItemRequests(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong()))
                .thenReturn(new ArrayList<>());
        mvc.perform(get("/requests/all")
                        .header(HEADER, "1"))
                .andExpect(status().isOk())
                .andDo(print());
        Mockito.verify(itemRequestService).getAllItemRequests(0, 10, 1L);
    }

    @Test
    void testGetItemRequestById() throws Exception {
        Mockito
                .when(itemRequestService.getItemRequestById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemRequestDtoResponse);
        mvc.perform(get("/requests/{requestId}", 1L)
                        .header(HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoResponse.getDescription())));
    }
}
