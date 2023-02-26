package ru.practicum.ewm.service.request.model;

import ru.practicum.ewm.service.request.dto.RequestDto;
import ru.practicum.ewm.service.utills.MainServiceDateTimeFormatter;

public class RequestMapper {

    public static RequestDto toRequestDto(Request request) {
        return RequestDto
                .builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .created(MainServiceDateTimeFormatter.dateTimeToString(request.getCreateDate()))
                .status(request.getStatus().toString())
                .build();
    }
}
