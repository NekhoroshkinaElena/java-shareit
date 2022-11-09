package ru.practicum.shareit.item.comment;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class CommentDtoOutput {
    long id;
    String text;
    String authorName;
    LocalDateTime created;
}
