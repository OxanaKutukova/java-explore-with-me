package ru.practicum.ewm.service.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class NewCompilationDto {
    private Long id;
    private List<Long> events;
    private boolean pinned;

    @NotBlank
    private String title;
}
