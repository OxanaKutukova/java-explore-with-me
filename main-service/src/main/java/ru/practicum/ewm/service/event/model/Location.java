package ru.practicum.ewm.service.event.model;

import lombok.*;

import javax.persistence.Embeddable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Embeddable
public class Location {
    private Double lat;
    private Double lon;
}
