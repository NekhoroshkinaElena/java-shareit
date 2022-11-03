package ru.practicum.shareit.item.comment;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
public class CommentDto {

    @NotNull
    @NotEmpty
    @NotBlank
    private String text;
}
