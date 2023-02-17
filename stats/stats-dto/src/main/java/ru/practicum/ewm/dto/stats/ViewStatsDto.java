package ru.practicum.ewm.dto.stats;

import lombok.*;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ViewStatsDto {
    @NotBlank
    private String app;

    @NotBlank
    private String uri;

    @NotBlank
    private long hits;
}
