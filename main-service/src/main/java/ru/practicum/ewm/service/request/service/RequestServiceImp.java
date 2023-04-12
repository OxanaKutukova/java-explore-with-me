package ru.practicum.ewm.service.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.model.EventStatus;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.exception.*;
import ru.practicum.ewm.service.request.dto.RequestDto;
import ru.practicum.ewm.service.request.model.Request;
import ru.practicum.ewm.service.request.model.RequestMapper;
import ru.practicum.ewm.service.request.model.RequestStatus;
import ru.practicum.ewm.service.request.repository.RequestRepository;
import ru.practicum.ewm.service.user.model.User;
import ru.practicum.ewm.service.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class RequestServiceImp implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public RequestDto create(Long userId, Long eventId) {

        final User user = getUserById(userId);
        final Event event = getEventById(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Нельзя добавить запрос на участие в своем событии");
        }
        if (!requestRepository.findByEvent_IdAndRequesterId(eventId, userId).isEmpty()) {
            throw new ConflictException("Нельзя добавить повторный запрос");
        }
        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }
        if (event.getConfirmedRequests() == event.getParticipantLimit()) {
            throw new ConflictException("Достигнут лимит запросов на участие");
        }

        Request request = new Request();

        if (event.getRequestModeration()) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        request.setEvent(event);
        request.setRequester(user);
        request.setCreateDate(LocalDateTime.now());

        final Request requestS = requestRepository.save(request);

        int confirmedRequest = requestRepository.countByEvent_IdAndStatus(eventId, RequestStatus.CONFIRMED);
        event.setConfirmedRequests(confirmedRequest);
        eventRepository.save(event);

        return RequestMapper.toRequestDto(requestS);

    }

    @Override
    public List<RequestDto> getAll(Long userId) {
        //Проверим пользователя
        throwIfNotExistUser(userId);
        List<Request> requests = requestRepository.findAllByRequesterId(userId);

        return requests
                .stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public RequestDto cancel(Long userId, Long requestId) {
        //Проверим пользователя
        throwIfNotExistUser(userId);
        final Request request = getRequestById(requestId);

        if (!request.getRequester().getId().equals(userId)) {
            throw new ForbiddenException("Нельзя отменить чужую заявку");
        }
        if (request.getStatus().equals(RequestStatus.REJECTED) || request.getStatus().equals(RequestStatus.CANCELED)) {
            throw new BadRequestException("Заявка уже отклонена");
        }

        request.setStatus(RequestStatus.CANCELED);
        requestRepository.save(request);

        return RequestMapper.toRequestDto(request);
    }

    private Request getRequestById(Long requestId) {
        return  requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка на участие не найдена или недоступна"));
    }

    private void throwIfNotExistUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден или недоступен"));
    }

    private User getUserById(Long userId) {
        return  userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден или недоступен"));
    }

    private Event getEventById(Long eventId) {
        return  eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено или недоступно"));
    }
}
