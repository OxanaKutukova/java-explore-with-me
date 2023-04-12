package ru.practicum.ewm.service.comment.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.comment.dto.CommentDto;
import ru.practicum.ewm.service.comment.dto.NewCommentDto;
import ru.practicum.ewm.service.comment.dto.UpdateCommentDto;
import ru.practicum.ewm.service.comment.model.Comment;
import ru.practicum.ewm.service.comment.model.CommentMapper;
import ru.practicum.ewm.service.comment.repository.CommentRepository;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.exception.ConflictException;
import ru.practicum.ewm.service.exception.NotFoundException;
import ru.practicum.ewm.service.user.model.User;
import ru.practicum.ewm.service.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;


    @Override
    @Transactional
    public CommentDto create(Long userId, Long eventId, NewCommentDto newCommentDto) {
        final User user = getUserById(userId);
        final Event event = getEventById(eventId);
        Comment commentU = CommentMapper.toComment(newCommentDto);
        commentU.setAuthor(user);
        commentU.setEvent(event);

        final Comment commentS = commentRepository.save(commentU);

        return CommentMapper.toCommentDto(commentS);
    }

    @Override
    @Transactional
    public CommentDto update(Long userId, Long commentId, UpdateCommentDto updateCommentDto) {
        throwIfNotExistUser(userId);

        final Comment comment = getCommentById(commentId);

        if (!userId.equals(comment.getAuthor().getId())) {
            throw new ConflictException("Редактировать комментарий может только его автор");
        }

        if (updateCommentDto.getText() != null) {
            comment.setText(updateCommentDto.getText());
        }
        final Comment commentSaved = commentRepository.save(comment);

        return CommentMapper.toCommentDto(commentSaved);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long commentId) {
        throwIfNotExistUser(userId);

        final Comment comment = getCommentById(commentId);

        if (!userId.equals(comment.getAuthor().getId())) {
            throw new ConflictException("Удалить комментарий может только его автор");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getAllByUser(Long userId, Pageable page) {
        throwIfNotExistUser(userId);

        return   commentRepository.findAllByAuthorId(userId, page)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteByAdmin(Long commentId) {

        final Comment comment = getCommentById(commentId);

        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getAllByEvent(Long eventId, Pageable pageable) {
        throwIfNotExistEvent(eventId);

        return   commentRepository.findAllByEventId(eventId, pageable)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getById(Long commentId) {

        return commentRepository.findById(commentId)
                .map(CommentMapper::toCommentDto)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден или недоступен"));

    }

    @Override
    public List<CommentDto> getByText(String text, Pageable pageable) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return commentRepository.search(text, pageable)
                                .stream()
                                .map(CommentMapper::toCommentDto)
                                .collect(Collectors.toList());

    }

    private Comment getCommentById(Long commentId) {
        return  commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден или недоступен"));
    }

    private User getUserById(Long userId) {
        return  userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден или недоступен"));
    }

    private void throwIfNotExistUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден или недоступен"));
    }

    private Event getEventById(Long eventId) {
        return  eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено или недоступно"));
    }

    private void throwIfNotExistEvent(Long eventId) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено или недоступно"));
    }

}
