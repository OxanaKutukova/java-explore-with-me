package ru.practicum.ewm.service.compilation.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class UpdateCompilationDto {
    private Long id;
    private List<Long> events;
    private boolean pinned;

    private String title;
}