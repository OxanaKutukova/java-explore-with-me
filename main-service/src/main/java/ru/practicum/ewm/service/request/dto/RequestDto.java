package ru.practicum.ewm.service.request.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RequestDto {

    private Long id;
    private Long event;
    private Long requester;
    private String created;
    private String status;
}
