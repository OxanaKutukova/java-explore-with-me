package ru.practicum.ewm.service.exception;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@ToString
public class ApiError {
    private final List<String> errors;
    private final String message;
    private final String reason;
    private final ErrorStatus status;
    private final LocalDateTime timestamp = LocalDateTime.now();
}
