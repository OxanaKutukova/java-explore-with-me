package ru.practicum.ewm.service.event.model;

public enum EventStatus {
        PENDING,
        PUBLISHED,
        CANCELED;

        public static EventStatus from(String text) {
                for (EventStatus status : EventStatus.values()) {
                        if (status.name().equalsIgnoreCase(text)) {
                                return status;
                        }
                }
                return null;
        }
}
