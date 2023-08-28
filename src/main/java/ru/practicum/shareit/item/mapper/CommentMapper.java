package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CommentMapper {
    public CommentResponseDto toResponseDto(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("Comment не может быть null.");
        }
        return CommentResponseDto.builder()
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
    }

    public Comment dtoToComment(CommentRequestDto commentRequestDto, User user, Item item, LocalDateTime createdTime) {
        Comment comment = new Comment();
        comment.setText(commentRequestDto.getText());
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(createdTime);
        return comment;
    }

    public List<CommentResponseDto> listCommentsToListResponse(Collection<Comment> comments) {
        return comments.stream().map(CommentMapper::toResponseDto).collect(Collectors.toList());
    }
}
