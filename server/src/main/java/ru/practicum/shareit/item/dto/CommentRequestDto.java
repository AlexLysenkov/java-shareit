package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentRequestDto {
    private String text;
}
