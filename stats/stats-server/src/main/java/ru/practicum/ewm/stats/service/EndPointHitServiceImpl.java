package ru.practicum.ewm.stats.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.stats.EndPointHitDto;
import ru.practicum.ewm.dto.stats.ViewStatsDto;
import ru.practicum.ewm.dto.stats.ViewStatsParamDto;
import ru.practicum.ewm.stats.model.EndPointHitMapper;

import ru.practicum.ewm.stats.model.EndPointHitStats;
import ru.practicum.ewm.stats.repository.EndPointHitRepository;
import ru.practicum.ewm.stats.utills.StatsDateTimeFormatter;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class EndPointHitServiceImpl implements EndPointHitService {

    private final EndPointHitRepository endPointHitRepository;

    @Transactional
    @Override
    public void save(EndPointHitDto endPointHitDto) {
        endPointHitRepository.save(EndPointHitMapper.toEndPointHit(endPointHitDto));
    }

    @Override
    public List<ViewStatsDto> getStats(ViewStatsParamDto viewStatsParamDto) {
        viewStatsParamDto.setStart(URLDecoder.decode(viewStatsParamDto.getStart(), StandardCharsets.UTF_8));
        viewStatsParamDto.setEnd(URLDecoder.decode(viewStatsParamDto.getEnd(), StandardCharsets.UTF_8));

        List<EndPointHitStats> endPointHits;
       if (viewStatsParamDto.getUris() != null && !viewStatsParamDto.isUnique()) {
            endPointHits = endPointHitRepository.findAllByTimestampBetweenAndUriIn(
                            StatsDateTimeFormatter.stringToDateTime(viewStatsParamDto.getStart()),
                            StatsDateTimeFormatter.stringToDateTime(viewStatsParamDto.getEnd()),
                            List.of(viewStatsParamDto.getUris()));
        } else if (viewStatsParamDto.getUris() != null && viewStatsParamDto.isUnique()) {
            endPointHits = endPointHitRepository.findAllByUrisAndUniqueIp(
                            StatsDateTimeFormatter.stringToDateTime(viewStatsParamDto.getStart()),
                            StatsDateTimeFormatter.stringToDateTime(viewStatsParamDto.getEnd()),
                            List.of(viewStatsParamDto.getUris()));
        } else if (viewStatsParamDto.getUris() == null && viewStatsParamDto.isUnique()) {
            endPointHits = endPointHitRepository.findAllByUniqueIp(
                            StatsDateTimeFormatter.stringToDateTime(viewStatsParamDto.getStart()),
                            StatsDateTimeFormatter.stringToDateTime(viewStatsParamDto.getEnd()));
        }  else {
            endPointHits = endPointHitRepository.findAllByTimestampBetween(
                            StatsDateTimeFormatter.stringToDateTime(viewStatsParamDto.getStart()),
                            StatsDateTimeFormatter.stringToDateTime(viewStatsParamDto.getEnd()));
        }
        List<ViewStatsDto> viewStats = endPointHits
                .stream()
                .map(EndPointHitMapper::toViewStatsDto)
                .collect(Collectors.toList());

        return viewStats;
    }

}
