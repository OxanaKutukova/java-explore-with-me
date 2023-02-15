package ru.practicum.ewm.dto.stats;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ViewStatsParamDto {
    private String start;
    private String end;
    private String[] uris;
    private boolean unique;
}
