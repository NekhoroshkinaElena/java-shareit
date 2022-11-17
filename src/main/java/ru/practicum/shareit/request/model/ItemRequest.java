package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
@Entity
@Table(name = "REQUESTS")
@NoArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "requestor", referencedColumnName = "id")
    private User requestor;

    @Column(name = "created")
    private LocalDateTime created;

    public ItemRequest(String description) {
        this.description = description;
    }
}
