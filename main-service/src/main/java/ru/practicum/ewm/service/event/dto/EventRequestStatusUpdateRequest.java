package ru.practicum.ewm.service.event.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class EventRequestStatusUpdateRequest {

    private Long[] requestIds;

    private String status;
}
