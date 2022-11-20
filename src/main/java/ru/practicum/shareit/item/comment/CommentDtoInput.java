package ru.practicum.shareit.item.comment;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CommentDtoInput {
    @NotNull
    @NotBlank
    private String text;
}
