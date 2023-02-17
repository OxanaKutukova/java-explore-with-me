package ru.practicum.ewm.stats.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class EndPointHitStats {

        private String app;
        private String uri;
        private long hits;

}
