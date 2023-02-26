package ru.practicum.ewm.service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.client.stats.StatsClient;
import ru.practicum.ewm.service.event.dto.*;
import ru.practicum.ewm.service.event.service.EventService;
import ru.practicum.ewm.service.request.dto.RequestDto;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventController {
    @Autowired
    private final EventService eventService;
    private final StatsClient statsClient;

    //Admin Редактировать событие и его статус (отклонить/опубликовать)
    @PatchMapping("/admin/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateAdmin(@PathVariable Long eventId,
                                    @RequestBody UpdateEventDto updateEventDto) {
        log.info("MainServer: Admin Редактировать событие с id: {} на {}", eventId, updateEventDto);
        EventFullDto resEventFullDto = eventService.updateAdmin(eventId, updateEventDto);
        log.info("MainServer: Admin Событие отредактировано. Результат = {}", resEventFullDto);

        return resEventFullDto;
    }

    //Admin Поиск событий
    @GetMapping("/admin/events")
    public List<EventFullDto> getAllAdmin(@RequestParam Optional<List<Long>> users,
                                          @RequestParam Optional<List<String>> states,
                                          @RequestParam Optional<List<Long>> categories,
                                          @RequestParam Optional<String> rangeStart,
                                          @RequestParam Optional<String> rangeEnd,
                                          @RequestParam(defaultValue = "0") int from,
                                          @RequestParam(defaultValue = "20") int size,
                                          HttpServletRequest httpRequest) {

        log.info("MainServer: Admin Получить все события");
        final Pageable pageable = PageRequest.of(from / size, size);
        List<EventFullDto> events = eventService.getAllAdmin(users, states, categories, rangeStart, rangeEnd, pageable);
        log.info("MainServer: Admin Получить все события. Результат = {}", events);

        return events;
    }

    //Public Получение событий с возможностью фильтрации
    @GetMapping("/events")
    public List<EventShortDto> getAllPublic(@RequestParam Optional<String> text,
                                      @RequestParam Optional<List<Long>> categories,
                                      @RequestParam Optional<Boolean> paid,
                                      @RequestParam Optional<String> rangeStart,
                                      @RequestParam Optional<String> rangeEnd,
                                      @RequestParam Optional<Boolean> onlyAvailable,
                                      @RequestParam Optional<String> sort,
                                      @RequestParam(defaultValue = "0") int from,
                                      @RequestParam(defaultValue = "20") int size,
                                      HttpServletRequest httpRequest) {

        log.info("MainServer: Public Получить все события с возможностью фильтрации");
        final Pageable pageable = PageRequest.of(from / size, size);
        List<EventShortDto> events = eventService.getAllPublic(text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable, sort, httpRequest, pageable);
        log.info("MainServer: Public Получены события. Результат = {}", events);

        return events;
    }

    //Получить подробную информацию об опубликованном событии
    @GetMapping("/events/{eventId}")
    public EventFullDto getPublicById(@PathVariable Long eventId,
                                HttpServletRequest httpRequest) {
        log.info("MainServer: Public Получить подробную информацию об опубликованном событии с id:{}", eventId);
        EventFullDto event = eventService.getPublicById(eventId, httpRequest);
        log.info("MainServer: Public Получена подробная информация об опубликованном событии. Результат = {}", event);

        return event;
    }

    //Получить события, добавленные текущим пользователем
    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getAllByUser(@PathVariable Long userId,
                                            @RequestParam(name = "from", defaultValue = "0") Integer from,
                                            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("MainServer: Private Получить события, добавленные пользователем c id: {}", userId);
        final Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        List<EventShortDto> allEvents = eventService.getAllByUser(userId, pageable);
        log.info("MainServer: Private Получен список всех событий, добавленных пользователем с id: {}. " +
                "Результат = {}", userId, allEvents);

        return allEvents;
    }

    //Добавить новое событие
    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable Long userId,
                               @Valid @RequestBody NewEventDto newEventDto) {
        log.info("MainServer: Private Добавить новое событие {} от пользователя с id: {}", newEventDto, userId);
        EventFullDto resEventFullDto = eventService.create(userId, newEventDto);
        log.info("MainServer: Private Событие добавлено: {}", newEventDto);

        return resEventFullDto;
    }

    //Получить информацию о конкретном событии пользователя
    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getById(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("MainServer: Private Получить информацию о событии с id:{} пользователя id: {}", eventId, userId);
        EventFullDto event = eventService.getById(eventId, userId);
        log.info("MainServer: Private Получена информация по событию с id: {}. " +
                "Результат = {}", eventId, event);

        return event;
    }

    //Изменить свое событие
    @PatchMapping("/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto update(@PathVariable Long userId,
                               @PathVariable Long eventId,
                               @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("MainServer: Private Изменить событие с id: {}, пользователя с id: {} на {}",
                eventId, userId, updateEventUserRequest);
        EventFullDto resEventFullDto = eventService.update(userId, eventId, updateEventUserRequest);
        log.info("MainServer: Private Изменено свое событие. Результат {}", resEventFullDto);

        return resEventFullDto;
    }

    //Получить информацию о запросах на участие в событии пользователя
    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<RequestDto> getAllRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("MainServer: Private Получить информацию о запросах на участие в событии с id:{} пользователя id: {}",
                eventId, userId);
        List<RequestDto> requests = eventService.getAllRequests(eventId, userId);
        log.info("MainServer:Private  Получена информация о запросах на участие в событии с id: {} пользователя id: {}. " +
                "Результат = {}", eventId, userId, requests);

        return requests;
    }

    //Изменить статус заявок на участие в событии
    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequests(@PathVariable Long userId,
                                       @PathVariable Long eventId,
                                       @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("MainServer: Изменить статус заявок на участие в событии с id: {} для запросов: {}",
                eventId, eventRequestStatusUpdateRequest);
        EventRequestStatusUpdateResult result = eventService.updateStatusRequest(userId, eventId, eventRequestStatusUpdateRequest);
        log.info("MainServer: Изменен статус заявок на участие в событии. Результат = {}", result);

        return result;
    }

}
