package ru.practicum.ewm.service.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.service.comment.model.Comment;


import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByAuthorId(Long userId, Pageable pageable);

    List<Comment> findAllByEventId(Long eventId, Pageable pageable);

    @Query("SELECT c FROM Comment c " +
            "WHERE UPPER(c.text) LIKE UPPER(CONCAT('%', ?1, '%')) ")
    List<Comment> search(String text, Pageable pageable);
}
