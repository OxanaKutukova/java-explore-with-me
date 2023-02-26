package ru.practicum.ewm.service.event.model;

import ru.practicum.ewm.service.category.model.CategoryMapper;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.dto.NewEventDto;
import ru.practicum.ewm.service.user.model.UserMapper;
import ru.practicum.ewm.service.utills.MainServiceDateTimeFormatter;

import java.time.LocalDateTime;

public class EventMapper {

    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto
                .builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(MainServiceDateTimeFormatter.dateTimeToString(event.getCreatedOn()))
                .description(event.getDescription())
                .eventDate(MainServiceDateTimeFormatter.dateTimeToString(event.getEventDate()))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(MainServiceDateTimeFormatter.dateTimeToString(event.getPublishedOn()))
                .requestModeration(event.getRequestModeration())
                .state(event.getStatus().toString())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static Event toEvent(NewEventDto newEventDto) {
        return Event
                .builder()
                .id(newEventDto.getId())
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .confirmedRequests(0)
                .createdOn(LocalDateTime.now())
                .eventDate(MainServiceDateTimeFormatter.stringToDateTime(newEventDto.getEventDate()))
                .location(newEventDto.getLocation())
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .status(EventStatus.PENDING)
                .title(newEventDto.getTitle())
                .views(0L)
                .build();
    }

    public static EventShortDto toEventShortDto(Event event) {

        return EventShortDto
                .builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(MainServiceDateTimeFormatter.dateTimeToString(event.getEventDate()))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

}
