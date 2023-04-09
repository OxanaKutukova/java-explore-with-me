package ru.practicum.ewm.service.comment.dto;

import lombok.*;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.user.dto.UserShortDto;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CommentDto {
    private Long id;
    private String text;
    private String insertDate;
    private UserShortDto author;
    private EventShortDto event;
}
