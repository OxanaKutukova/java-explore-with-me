package ru.practicum.ewm.stats.model;

import ru.practicum.ewm.dto.stats.EndPointHitDto;
import ru.practicum.ewm.dto.stats.ViewStatsDto;
import ru.practicum.ewm.stats.utills.StatsDateTimeFormatter;


public class EndPointHitMapper {

    public static EndPointHit toEndPointHit(EndPointHitDto endPointHitDto) {
        return EndPointHit
                .builder()
                .app(endPointHitDto.getApp())
                .uri(endPointHitDto.getUri())
                .ip(endPointHitDto.getIp())
                .timestamp(StatsDateTimeFormatter.stringToDateTime(endPointHitDto.getTimestamp()))
                .build();
    }

    public static ViewStatsDto toViewStatsDto(EndPointHitStats endPointHitStats) {
        return ViewStatsDto
                .builder()
                .app(endPointHitStats.getApp())
                .uri(endPointHitStats.getUri())
                .hits(endPointHitStats.getHits())
                .build();
    }

}
