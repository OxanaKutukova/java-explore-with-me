package ru.practicum.ewm.service.event.dto;

import lombok.*;
import ru.practicum.ewm.service.request.dto.RequestDto;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class EventRequestStatusUpdateResult {

    private List<RequestDto> confirmedRequests;
    private List<RequestDto> rejectedRequests;
}
