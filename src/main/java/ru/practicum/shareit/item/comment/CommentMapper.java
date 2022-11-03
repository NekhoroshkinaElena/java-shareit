package ru.practicum.shareit.item.comment;

import java.time.LocalDateTime;

public class CommentMapper {

    public static CommentDtoOutput commentDtoOutput(Comment comment) {
        return new CommentDtoOutput(comment.getId(),
                comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    }

    public static Comment toComment(CommentDto commentDto) {
        return new Comment(commentDto.getText(), LocalDateTime.now());
    }
}
