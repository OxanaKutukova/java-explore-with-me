package ru.practicum.ewm.service.utills;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MainServiceDateTimeFormatter {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String dateTimeToString(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(dateTimeFormatter);
    }

    public static LocalDateTime stringToDateTime(String strDateTime) {
        if (strDateTime == null) {
            return null;
        }
        String[] lines = strDateTime.split(" ");
        return LocalDateTime.of(LocalDate.parse(lines[0]), LocalTime.parse(lines[1]));
    }

}
