package ru.practicum.ewm.service.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.service.event.dto.*;
import ru.practicum.ewm.service.request.dto.RequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    EventFullDto updateAdmin(Long eventId, UpdateEventDto updateEventDto);

    List<EventShortDto> getAllPublic(String text, List<Long> categories, Boolean paid,
                                     String rangeStart, String rangeEnd,
                                     Boolean onlyAvailable, String sort,
                                     HttpServletRequest httpRequest, Pageable pageable);

    List<EventFullDto> getAllAdmin(List<Long> users,
                                          List<String> states,
                                          List<Long> categories,
                                          String rangeStart,
                                          String rangeEnd,
                                          Pageable pageable);

    EventFullDto getPublicById(Long eventId, HttpServletRequest httpRequest);

    EventFullDto create(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getAllByUser(Long userId, Pageable page);

    EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    EventFullDto getById(Long eventId, Long userId);

    List<RequestDto> getAllRequests(Long eventId, Long userId);




    EventRequestStatusUpdateResult updateStatusRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

}
