package ru.practicum.ewm.service.request.service;

import ru.practicum.ewm.service.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto create(Long userId, Long eventId);

    List<RequestDto> getAll(Long userId);

    RequestDto cancel(Long userId, Long requestId);
}
