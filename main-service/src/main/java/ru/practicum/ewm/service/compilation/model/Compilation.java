package ru.practicum.ewm.service.compilation.model;

import lombok.*;
import ru.practicum.ewm.service.event.model.Event;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode (of = "id")
@Entity
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compilation_id")
    private Long id;

    @ManyToMany
    @JoinTable(name = "event_compilation", joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> events;

    private boolean pinned;

    @Column(name = "title", nullable = false, length = 120)
    private String title;
}
