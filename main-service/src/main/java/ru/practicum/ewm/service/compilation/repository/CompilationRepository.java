package ru.practicum.ewm.service.compilation.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.service.compilation.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    List<Compilation> findAllByPinnedIs(boolean pinned, Pageable pageable);

    @Modifying
    @Query(value = "DELETE FROM event_compilation WHERE compilation_id = ?1", nativeQuery = true)
    void deleteEventCompilation(Long compId);

    @Modifying
    @Query(value = "INSERT INTO event_compilation (compilation_id, event_id) VALUES (?1, ?2)", nativeQuery = true)
    void addEventCompilation(Long compilationId, Long eventId);
}
