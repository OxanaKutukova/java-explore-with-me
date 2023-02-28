package ru.practicum.ewm.service.exception;

public class HostUnreachableException extends RuntimeException {

    public HostUnreachableException(String message) {
        super(message);
    }
}
