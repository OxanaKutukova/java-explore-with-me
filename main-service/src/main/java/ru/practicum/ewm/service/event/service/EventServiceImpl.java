package ru.practicum.ewm.service.event.service;

import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import ru.practicum.ewm.client.stats.StatsClient;
import ru.practicum.ewm.dto.stats.ViewStatsDto;
import ru.practicum.ewm.dto.stats.ViewStatsParamDto;
import ru.practicum.ewm.service.category.model.Category;
import ru.practicum.ewm.service.category.repository.CategoryRepository;
import ru.practicum.ewm.service.event.dto.*;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.model.EventMapper;
import ru.practicum.ewm.service.event.model.EventStatus;
import ru.practicum.ewm.service.event.model.QEvent;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.exception.*;
import ru.practicum.ewm.service.request.dto.RequestDto;
import ru.practicum.ewm.service.request.model.Request;
import ru.practicum.ewm.service.request.model.RequestMapper;
import ru.practicum.ewm.service.request.model.RequestStatus;
import ru.practicum.ewm.service.request.repository.RequestRepository;
import ru.practicum.ewm.service.user.model.User;
import ru.practicum.ewm.service.user.repository.UserRepository;
import ru.practicum.ewm.service.utills.MainServiceDateTimeFormatter;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    private final StatsClient statsClient;

    @Override
    @Transactional
    public EventFullDto updateAdmin(Long eventId, UpdateEventDto updateEventDto) {

        final Event event = getEventById(eventId);
        if (updateEventDto.getEventDate() != null) {
            if (MainServiceDateTimeFormatter.stringToDateTime(updateEventDto.getEventDate())
                    .isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("Дата и время начала события не может быть раньше, " +
                        "чем через два часа от текущего момента");
            }
        }
        if (updateEventDto.getStateAction().equals("PUBLISH_EVENT")) {
            if (!event.getStatus().equals(EventStatus.PENDING)) {
                throw new ValidationException("Событие можно публиковать, только если оно в состоянии ожидания публикации");
            }
            event.setStatus(EventStatus.PUBLISHED);
        }
        if (updateEventDto.getStateAction().equals("REJECT_EVENT")) {
            if (event.getStatus().equals(EventStatus.PUBLISHED)) {
                throw new ValidationException("Событие можно отклонить, только если оно еще не опубликовано");
            }
            event.setStatus(EventStatus.CANCELED);
        }

        if (updateEventDto.getAnnotation() != null) {
            event.setAnnotation(updateEventDto.getAnnotation());
        }
        if (updateEventDto.getCategory() != null) {
            event.setCategory(getCategoryById(updateEventDto.getCategory()));
        }
        if (updateEventDto.getDescription() != null) {
            event.setDescription(updateEventDto.getDescription());
        }
        if (updateEventDto.getEventDate() != null) {
            event.setEventDate(MainServiceDateTimeFormatter.stringToDateTime(updateEventDto.getEventDate()));
        }
        if (updateEventDto.getPaid() != null) {
            event.setPaid(updateEventDto.getPaid());
        }
        if (updateEventDto.getParticipantLimit() != 0) {
            event.setParticipantLimit(updateEventDto.getParticipantLimit());
        }
        if (updateEventDto.getTitle() != null) {
            event.setTitle(updateEventDto.getTitle());
        }
        Optional.ofNullable(updateEventDto.getLocation()).ifPresent(event::setLocation);
        Optional.ofNullable(updateEventDto.getRequestModeration()).ifPresent(event::setRequestModeration);

        final Event eventSaved = eventRepository.save(event);

        return EventMapper.toEventFullDto(eventSaved);
    }

    @Override
    public List<EventFullDto> getAllAdmin(Optional<List<Long>> usersOptional,
                                          Optional<List<String>> statesOptional,
                                          Optional<List<Long>> categoriesOptional,
                                          Optional<String> rangeStartOptional,
                                          Optional<String> rangeEndOptional,
                                          Pageable pageable) {

        //Соберем фильтр из пришедших условий
        BooleanBuilder builder = new BooleanBuilder();

        usersOptional.ifPresent(users -> builder.and(QEvent.event.initiator.id.in(users)));

        statesOptional.ifPresent(states -> builder.and(QEvent.event.status.in(
                states.stream()
                        .map(EventStatus::from)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))));

        categoriesOptional.ifPresent(categories -> builder.and(QEvent.event.category.id.in(categories)));

        rangeStartOptional.ifPresent(start -> builder.and(QEvent.event.eventDate
                .after(MainServiceDateTimeFormatter.stringToDateTime(start))));

        rangeEndOptional.ifPresent(end -> builder.and(QEvent.event.eventDate
                .before(MainServiceDateTimeFormatter.stringToDateTime(end))));

        List<Event> events = eventRepository.findAll(builder, pageable)
                .stream()
                .collect(Collectors.toList());

        return events
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getAllPublic(Optional<String> textOptional,
                                            Optional<List<Long>> categoriesOptional,
                                            Optional<Boolean> paidOptional,
                                            Optional<String> rangeStartOptional,
                                            Optional<String> rangeEndOptional,
                                            Optional<Boolean> onlyAvailableOptional,
                                            Optional<String> sortOptional,
                                            HttpServletRequest httpRequest,
                                            Pageable pageable) {

        //Сохраним в сервисе статистики информацию о том, что по этому эндпоинту был осуществлен и обработан запрос
        try {
            statsClient.addHit(httpRequest);
        } catch (RestClientException e) {
            throw new HostUnreachableException("Нет соединения с сервисом статистики");
        }

        //Соберем фильтр из пришедших условий
        BooleanBuilder builder = new BooleanBuilder();

        textOptional.ifPresent(text -> builder.and(QEvent.event.annotation.likeIgnoreCase(text)
                .or(QEvent.event.description.likeIgnoreCase(text))));

        categoriesOptional.ifPresent(categories -> builder.and(QEvent.event.category.id.in(categories)));

        paidOptional.ifPresent(paid -> builder.and(QEvent.event.paid.eq(paid)));

        rangeStartOptional.ifPresent(start -> builder.and(QEvent.event.eventDate
                .after(MainServiceDateTimeFormatter.stringToDateTime(start))));

        rangeEndOptional.ifPresent(end -> builder.and(QEvent.event.eventDate
                .before(MainServiceDateTimeFormatter.stringToDateTime(end))));

        List<Event> events = eventRepository.findAll(builder, pageable)
                .stream()
                .collect(Collectors.toList());

        //Начинаем сбор входных параметров для получения статистики
        //Заполняем список uri
        final List<String> uris = events
                .stream()
                .map(Event::getId)
                .map(s -> httpRequest.getRequestURI() + "/" + s)
                .collect(Collectors.toList());


        final ViewStatsParamDto paramStats =  ViewStatsParamDto
                .builder()
                .start(MainServiceDateTimeFormatter
                        .dateTimeToString(LocalDateTime.of(1970, 1, 1, 0, 0, 0)))
                .end(MainServiceDateTimeFormatter.dateTimeToString(LocalDateTime.now()))
                .uris(uris.toArray(new String[0]))
                .unique(false)
                .build();

        try {
            List<ViewStatsDto> stats = statsClient.getStatsHit(paramStats);
            Map<Long, Long> viewsMap = Optional.ofNullable(stats).orElse(new ArrayList<>())
                    .stream()
                    .collect(Collectors.toMap(views1 -> Long.getLong(views1.getUri().split("/")[1]),
                            ViewStatsDto::getHits));
            //Проставим количество просмотров и количество уже одобренных заявок на участие
            for (Event event : events) {
                event.setViews(viewsMap.getOrDefault(event.getId(), 0L));
                int confirmedRequest = requestRepository.countByEvent_IdAndStatus(event.getId(), RequestStatus.CONFIRMED);
                event.setConfirmedRequests(confirmedRequest);
                eventRepository.save(event);
            }

        } catch (RestClientException e) {
            throw new HostUnreachableException("Нет соединения с сервисом статистики");
        }

        //Сделаем сортировку, если она задана
        if (sortOptional.isPresent()) {
            switch (sortOptional.toString().toUpperCase()) {
                case "EVENT_DATE":
                    events.sort(Comparator.comparing(Event::getEventDate));
                    break;
                case "VIEWS":
                    events.sort(Comparator.comparing(Event::getViews));
                    break;
                default:
                    events.sort(Comparator.comparing(Event::getId));
            }
        }

        return events
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getPublicById(Long eventId, HttpServletRequest httpRequest) {
        final Event event = getEventById(eventId);

        if (!event.getStatus().equals(EventStatus.PUBLISHED)) {
            throw new NotFoundException("Запрошенное событие не находится в статусе опубликовано");
        }
        //Начинаем входные параметры для получения статистики
        final ViewStatsParamDto paramStats =  ViewStatsParamDto
                .builder()
                .start(MainServiceDateTimeFormatter
                        .dateTimeToString(LocalDateTime.of(1970, 1, 1, 0, 0, 0)))
                .end(MainServiceDateTimeFormatter.dateTimeToString(LocalDateTime.now()))
                .uris(new String[] {httpRequest.getRequestURI()})
                .unique(false)
                .build();

        try {
            //Сохраним в сервисе статистики информацию о том, что по этому эндпоинту был осуществлен и обработан запрос
            statsClient.addHit(httpRequest);

            //Получим статистику из сервиса статистики
            List<ViewStatsDto> stats = statsClient.getStatsHit(paramStats);
            if (!stats.isEmpty()) {
                event.setViews(stats.get(0).getHits());
            } else {
                event.setViews(0L);
            }
            int confirmedRequest = requestRepository.countByEvent_IdAndStatus(eventId, RequestStatus.CONFIRMED);
            event.setConfirmedRequests(confirmedRequest);
            eventRepository.save(event);
        } catch (RestClientException e) {
            throw new HostUnreachableException("Нет соединения с сервисом статистики");
        }

        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getAllByUser(Long userId, Pageable page) {
        throwIfNotExistUser(userId);

        return   eventRepository.findAllByInitiatorId(userId, page)
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        final User user = getUserById(userId);
        if (MainServiceDateTimeFormatter.stringToDateTime(newEventDto.getEventDate())
                .isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата и время начала события не может быть раньше, " +
                    "чем через два часа от текущего момента");
        }
        Event eventU = EventMapper.toEvent(newEventDto);
        eventU.setInitiator(user);
        eventU.setCategory(getCategoryById(newEventDto.getCategory()));

        final Event eventS = eventRepository.save(eventU);

        return EventMapper.toEventFullDto(eventS);
    }

    @Override
    public EventFullDto getById(Long eventId, Long userId) {
        throwIfNotExistUser(userId);
        throwIfNotExistEvent(eventId);
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId);

        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        throwIfNotExistUser(userId);
        if (updateEventUserRequest.getEventDate() != null) {
            if (MainServiceDateTimeFormatter.stringToDateTime(updateEventUserRequest.getEventDate())
                    .isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("Дата и время начала события не может быть раньше, " +
                        "чем через два часа от текущего момента");
            }
        }

        final Event event = getEventById(eventId);

        if (!userId.equals(event.getInitiator().getId())) {
            throw new ValidationException("Редактировать событие может только его инициатор");
        }
        if (event.getStatus() == EventStatus.PUBLISHED) {
            throw new ValidationException("Редактировать можно только отмененные события или события, которые находятся" +
                    " на модерации");
        }

        if (updateEventUserRequest.getStateAction() != null) {
            if (!(updateEventUserRequest.getStateAction().toUpperCase().equals("SEND_TO_REVIEW") ||
                    updateEventUserRequest.getStateAction().toUpperCase().equals("CANCEL_REVIEW"))) {
                throw new ValidationException("Переданы некорректные данные в значении параметра stateAction");
            }
        }

        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(getCategoryById(updateEventUserRequest.getCategory()));
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(MainServiceDateTimeFormatter.stringToDateTime(updateEventUserRequest.getEventDate()));
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != 0) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }

        if (updateEventUserRequest.getStateAction().toUpperCase().equals("SEND_TO_REVIEW")) {
            event.setStatus(EventStatus.PENDING);
        }
        if (updateEventUserRequest.getStateAction().toUpperCase().equals("CANCEL_REVIEW")) {
            event.setStatus(EventStatus.CANCELED);
        }

        final Event eventSaved = eventRepository.save(event);

        return EventMapper.toEventFullDto(eventSaved);
    }

    @Override
    public List<RequestDto> getAllRequests(Long eventId, Long userId) {
        throwIfNotExistUser(userId);
        final Event event = getEventById(eventId);

        if (!userId.equals(event.getInitiator().getId())) {
            throw new BadRequestException("Просматривать заявки на событие может только инициатор");
        }
        List<Request> requests = requestRepository.findAllByEventId(eventId);

        return requests
                .stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateStatusRequest(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        throwIfNotExistUser(userId);
        final Event event = getEventById(eventId);
        if (!userId.equals(event.getInitiator().getId())) {
            throw new ForbiddenException("Редактировать событие может только его инициатор");
        }
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new ValidationException("Этому запросу нельзя изменить статус");
        }

        final String status = eventRequestStatusUpdateRequest.getStatus();
        if (status.equals("CONFIRMED")) {
            if (event.getParticipantLimit() == event.getConfirmedRequests()) {
                throw new ValidationException("Достигнут лимит запросов на участие в событии");
            }

            for (int i = 0; i < eventRequestStatusUpdateRequest.getRequestIds().length; i++) {

                final Request request = getRequestById(eventRequestStatusUpdateRequest.getRequestIds()[i]);
                if (!request.getStatus().equals(RequestStatus.PENDING)) {
                    throw new ValidationException("Запрос не в статусе ожидания");
                }
                if (!request.getEvent().getId().equals(event.getId())) {
                    throw new ValidationException("Запрос не соответствует событию");
                }

                request.setStatus(RequestStatus.CONFIRMED);
                requestRepository.save(request);
                int confirmedRequest = requestRepository.countByEvent_IdAndStatus(eventId, RequestStatus.CONFIRMED);
                event.setConfirmedRequests(confirmedRequest);
                eventRepository.save(event);
                if (event.getParticipantLimit() == event.getConfirmedRequests()) {
                    requestRepository.saveAll(requestRepository.findByEvent_IdAndStatus(eventId, RequestStatus.PENDING)
                            .stream()
                            .peek(e -> e.setStatus(RequestStatus.CANCELED))
                            .collect(Collectors.toList()));
                    break;
                }
            }
        } else if (status.equals("REJECTED")) {

            for (int i = 0; i < eventRequestStatusUpdateRequest.getRequestIds().length; i++) {

                final Request request = getRequestById(eventRequestStatusUpdateRequest.getRequestIds()[i]);

                if (!request.getStatus().equals(RequestStatus.PENDING)) {
                    throw new ValidationException("Запрос не в статусе ожидания");
                }
                if (!request.getEvent().getId().equals(event.getId())) {
                    throw new ValidationException("Запрос не соответствует событию");
                }
                request.setStatus(RequestStatus.REJECTED);
                requestRepository.save(request);
            }
        } else {
            throw new BadRequestException("Передан некооректный статус для изменения");
        }
        List<RequestDto> requestConfirmed = requestRepository.findByEvent_IdAndStatus(eventId, RequestStatus.CONFIRMED)
                .stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
        List<RequestDto> rejectedConfirmed = requestRepository.findByEvent_IdAndStatus(eventId, RequestStatus.REJECTED)
                .stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());

        return EventRequestStatusUpdateResult
                .builder()
                .confirmedRequests(requestConfirmed)
                .rejectedRequests(rejectedConfirmed)
                .build();
    }

    private Event getEventById(Long eventId) {
        return  eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено или недоступно"));
    }

    private void throwIfNotExistEvent(Long eventId) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено или недоступно"));
    }

    private User getUserById(Long userId) {
        return  userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден или недоступен"));
    }

    private void throwIfNotExistUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден или недоступен"));
    }

    private Category getCategoryById(Long categoryId) {
        return  categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория с id=" + categoryId + " не найдена"));
    }

    private Request getRequestById(Long requestId) {
        return  requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка на участие не найдена или недоступна"));
    }
}
