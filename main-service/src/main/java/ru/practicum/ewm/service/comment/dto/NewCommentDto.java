package ru.practicum.ewm.service.comment.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class NewCommentDto {

    private Long id;

    @NotBlank
    @Size(min = 2, max = 4000)
    private String text;
}
