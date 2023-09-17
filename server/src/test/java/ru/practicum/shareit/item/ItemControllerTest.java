package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utils.Constants.HEADER;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;
    private ItemRequestDto itemRequestDto;
    private CommentResponseDto commentResponseDto;

    @BeforeEach
    void setUp() {
        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .build();
        commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .text("text")
                .build();
    }

    @Test
    void testCreateItem() throws Exception {
        Mockito
                .when(itemService.createItemDto(Mockito.any(), Mockito.any())).thenReturn(new ItemResponseDto());
        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
        Mockito.verify(itemService).createItemDto(itemRequestDto, 1L);
    }

    @Test
    void testUpdateItem() throws Exception {
        itemRequestDto.setName("NewName");
        Mockito
                .when(itemService.updateItemDto(Mockito.anyLong(), Mockito.any(), Mockito.anyLong()))
                .thenReturn(new ItemResponseDto());
        mvc.perform(patch("/items/{id}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void testGetItemById() throws Exception {
        Mockito
                .when(itemService.getItemDtoById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(new ItemFullResponseDto());
        mvc.perform(get("/items/{id}", itemRequestDto.getId())
                        .content(objectMapper.writeValueAsBytes(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
        Mockito.verify(itemService).getItemDtoById(itemRequestDto.getId(), 1L);
    }

    @Test
    void testGetAllOwnerItems() throws Exception {
        Mockito
                .when(itemService.getAllUserItemsDto(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(new ArrayList<>());
        mvc.perform(get("/items")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
        Mockito.verify(itemService).getAllUserItemsDto(1L, 0, 10);
    }

    @Test
    void testSearchItems() throws Exception {
        String text = "text";
        Mockito
                .when(itemService.searchItemsDto(Mockito.anyString(), Mockito.anyLong(), Mockito.anyInt(),
                        Mockito.anyInt())).thenReturn(new ArrayList<>());
        mvc.perform(get("/items/search?text=text")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
        Mockito.verify(itemService).searchItemsDto(text, 1L, 0, 10);
    }

    @Test
    void testCreateComment() throws Exception {
        Mockito
                .when(itemService.createComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(commentResponseDto);
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("new next");
        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .content(objectMapper.writeValueAsString(commentRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
