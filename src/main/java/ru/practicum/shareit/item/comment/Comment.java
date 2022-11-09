package ru.practicum.shareit.item.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "text_comment")
    private String text;
    @ManyToOne
    private Item item;
    @ManyToOne
    private User author;
    @Column(name = "created")
    private LocalDateTime created;

    public Comment(String text, LocalDateTime created) {
        this.text = text;
        this.created = created;
    }
}
