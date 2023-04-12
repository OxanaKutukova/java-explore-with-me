package ru.practicum.ewm.service.comment.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.service.comment.dto.CommentDto;
import ru.practicum.ewm.service.comment.dto.NewCommentDto;
import ru.practicum.ewm.service.comment.dto.UpdateCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto create(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto update(Long userId, Long commentId, UpdateCommentDto updateCommentDto);

    void delete(Long userId, Long commentId);

    List<CommentDto> getAllByUser(Long userId, Pageable page);

    void deleteByAdmin(Long commentId);

    List<CommentDto> getAllByEvent(Long eventId, Pageable pageable);

    CommentDto getById(Long commentId);

    List<CommentDto> getByText(String text, Pageable pageable);
}
