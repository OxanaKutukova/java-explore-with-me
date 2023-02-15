package ru.practicum.ewm.stats.service;

import ru.practicum.ewm.dto.stats.EndPointHitDto;
import ru.practicum.ewm.dto.stats.ViewStatsDto;
import ru.practicum.ewm.dto.stats.ViewStatsParamDto;
import ru.practicum.ewm.stats.model.EndPointHitStats;

import java.util.List;

public interface EndPointHitService {
    void save(EndPointHitDto endPointHitDto);

    List<ViewStatsDto> getStats(ViewStatsParamDto viewStatsParamDto);

}
