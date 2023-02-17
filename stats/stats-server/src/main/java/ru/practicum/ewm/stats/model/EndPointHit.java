package ru.practicum.ewm.stats.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode (of = "id")
@Entity
@Table(name = "endpoint_hit")

public class EndPointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "app", nullable = false, length = 100)
    private String app;

    @Column(name = "uri", nullable = false, length = 150)
    private String uri;

    @Column(name = "ip", nullable = false, length = 30)
    private String ip;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

}
