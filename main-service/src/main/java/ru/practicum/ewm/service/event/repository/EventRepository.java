package ru.practicum.ewm.service.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.service.event.model.Event;

import java.util.List;


public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    Event findByInitiatorIdAndId(Long userId, Long eventId);

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

}
