package ru.practicum.ewm.service.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.request.dto.RequestDto;
import ru.practicum.ewm.service.request.service.RequestService;


import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class RequestController {

    @Autowired
    private final RequestService requestService;

    //Добавить запрос от пользователя на участие в событии
    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto create(@PathVariable (required = true) Long userId,
                             @Valid @RequestParam(name = "eventId", required = true)  Long eventId) {
        log.info("MainServer: Добавить запрос от пользователя с id: {} на участие в событии с id: {}", userId, eventId);
        RequestDto resRequestDto = requestService.create(userId, eventId);
        log.info("MainServer: Запрос: {} на участие в событии добавлен.", resRequestDto);

        return resRequestDto;
    }

    //Получить заявки на участие в чужих событиях
    @GetMapping("/users/{userId}/requests")
    public List<RequestDto> getAll(@PathVariable Long userId) {
        log.info("MainServer: Получить заявки на участие в чужих событиях");
        List<RequestDto> allRequests = requestService.getAll(userId);
        log.info("MainServer: Получен список всех заявок на участие в чужих событиях. Результат = {}", allRequests);

        return allRequests;
    }

    //Отмена своего запроса на участие в событии
    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public RequestDto update(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("MainServer: Отменить запрос пользователя с Id: {} на участие в событии с Id: {}", userId, requestId);
        RequestDto resRequestDto = requestService.cancel(userId, requestId);
        log.info("MainServer: Отменен запрос пользователя на участие в событии");

        return resRequestDto;
    }

}
