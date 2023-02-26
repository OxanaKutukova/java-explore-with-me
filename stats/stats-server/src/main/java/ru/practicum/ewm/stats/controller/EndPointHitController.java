package ru.practicum.ewm.stats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.stats.EndPointHitDto;
import ru.practicum.ewm.dto.stats.ViewStatsDto;
import ru.practicum.ewm.dto.stats.ViewStatsParamDto;
import ru.practicum.ewm.stats.service.EndPointHitService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class EndPointHitController {

    @Autowired
    private final EndPointHitService endPointHitService;

    //Сохранение информации о том, что на uri конкретного сервиса был отправлен запрос пользователем
    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody EndPointHitDto endPointHitDto) {
        log.info("StatsServer: Сохранить информацию {}", endPointHitDto);
        endPointHitService.save(endPointHitDto);
    }

    //Получение статистики по посещениям
    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(ViewStatsParamDto viewStatsParamDto) {
        log.info("StatsServer: Получить статистику по посещениям = {}", viewStatsParamDto);
        List<ViewStatsDto> stats = endPointHitService.getStats(viewStatsParamDto);
        log.info("StatsServer: Получить статистику по посещениям. Результат = {}", stats);

        return stats;
    }

}
