package ru.practicum.shareit.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CommentMapperTest {
    private Comment comment;
    private CommentResponseDto commentResponseDto;
    private CommentRequestDto commentRequestDto;
    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        item = Item.builder()
                .id(1L)
                .name("Name")
                .description("New Description")
                .available(true)
                .owner(User.builder().build())
                .build();

        user = User.builder()
                .id(1L)
                .name("Name")
                .email("some@email.ru")
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("new text")
                .author(user)
                .created(LocalDateTime.now())
                .item(item)
                .build();

        commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .text("text")
                .authorName("Name")
                .created(LocalDateTime.now())
                .build();

        commentRequestDto = CommentRequestDto.builder()
                .text("text")
                .build();

        commentResponseDto = CommentMapper.toResponseDto(comment);
    }

    @Test
    void testToResponseDto() {
        assertNotNull(comment);
        assertEquals(comment.getId(), commentResponseDto.getId());
        assertEquals(comment.getText(), commentResponseDto.getText());
        assertEquals(comment.getAuthor().getName(), commentResponseDto.getAuthorName());
        assertEquals(comment.getCreated(), commentResponseDto.getCreated());
    }

    @Test
    void dtoToComment() {
        comment = CommentMapper.dtoToComment(commentRequestDto, user, item,
                LocalDateTime.of(2023, 11, 29, 15, 20, 13));
        assertNotNull(comment);
        assertEquals("text", comment.getText());
        assertEquals(user, comment.getAuthor());
        assertEquals(item, comment.getItem());
        assertEquals(LocalDateTime.of(2023, 11, 29, 15, 20, 13),
                comment.getCreated());
    }

    @Test
    void testToUserResponseDtoList() {
        List<Comment> commentList = new ArrayList<>();
        assertTrue(CommentMapper.listCommentsToListResponse(commentList).isEmpty());
        commentList.add(comment);
        List<CommentResponseDto> comments = CommentMapper.listCommentsToListResponse(commentList);
        assertEquals(1, comments.size());
        assertEquals("Name", comments.get(0).getAuthorName());
        assertEquals("new text", comments.get(0).getText());
        assertEquals(1L, comments.get(0).getId());
    }
}
