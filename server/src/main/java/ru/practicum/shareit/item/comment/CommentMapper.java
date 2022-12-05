package ru.practicum.shareit.item.comment;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {

    public static CommentDtoOutput commentDtoOutput(Comment comment) {
        return new CommentDtoOutput(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public static Comment toComment(CommentDtoInput commentDtoInput) {
        return new Comment(
                commentDtoInput.getText(),
                LocalDateTime.now());
    }
}
