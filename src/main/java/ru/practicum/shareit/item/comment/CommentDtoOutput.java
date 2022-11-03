package ru.practicum.shareit.item.comment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDtoOutput {
    private long id;
    private String text;
    private String authorName;
    private LocalDateTime created;

    public CommentDtoOutput(long id, String text, String author, LocalDateTime time) {
        this.id = id;
        this.text = text;
        this.authorName = author;
        this.created = time;
    }
}
