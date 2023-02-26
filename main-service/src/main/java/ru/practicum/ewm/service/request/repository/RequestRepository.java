package ru.practicum.ewm.service.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.service.request.model.Request;
import ru.practicum.ewm.service.request.model.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findByEvent_IdAndStatus(Long id, RequestStatus status);

    List<Request> findByEvent_IdAndRequesterId(Long eventId, Long userId);

    int countByEvent_IdAndStatus(Long eventId, RequestStatus status);
}
