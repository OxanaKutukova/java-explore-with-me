package ru.practicum.ewm.service.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.comment.dto.CommentDto;
import ru.practicum.ewm.service.comment.dto.NewCommentDto;
import ru.practicum.ewm.service.comment.dto.UpdateCommentDto;
import ru.practicum.ewm.service.comment.service.CommentService;


import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class CommentController {
    @Autowired
    private final CommentService commentService;

    //Получить все комментарии пользователя
    @GetMapping("/users/{userId}/comments")
    public List<CommentDto> getAllByUser(@PathVariable Long userId,
                                         @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("MainServer: Private Получить все комментарии пользователя id: {}", userId);
        final Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));

        List<CommentDto> comments = commentService.getAllByUser(userId, pageable);
        log.info("MainServer: Private  Получен список всех комментариев, созданных пользователем с id: {}.  " +
                "Результат = {}", userId, comments);

        return comments;
    }

    //Добавить комментарий пользователя на событие
    @PostMapping("/users/{userId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@PathVariable Long userId,
                             @Valid @RequestParam(name = "eventId", required = true)  Long eventId,
                             @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("MainServer: Private Добавить новый комментарий {} от пользователя с id: {} на событие с id: {}",
                newCommentDto, userId, eventId);
        CommentDto resCommentDto = commentService.create(userId, eventId, newCommentDto);
        log.info("MainServer: Private Комментарий добавлен: {} ", resCommentDto);

        return resCommentDto;
    }

    //Изменить комментарий пользователя на событие
    @PatchMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto update(@PathVariable Long userId,
                             @PathVariable Long commentId,
                             @Valid @RequestBody UpdateCommentDto updateCommentDto) {
        log.info("MainServer: Private Изменить комментарий с id: {}, пользователя с id: {} на {}",
                commentId, userId, updateCommentDto);
        CommentDto resCommentDto = commentService.update(userId, commentId, updateCommentDto);
        log.info("MainServer: Private Изменен свой комментарий. Результат {}", resCommentDto);

        return resCommentDto;
    }

    //Удалить комментарий пользователя на событие
    @DeleteMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId,
                       @PathVariable Long commentId) {
        log.info("MainServer: Private Удалить комментарий с id {} ", commentId);
        commentService.delete(userId, commentId);
        log.info("MainServer: Private Удален комментарий id {}", commentId);
    }

    //Удалить админом комментария пользователя
    @DeleteMapping("/admin/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByAdmin(@PathVariable Long commentId) {
        log.info("MainServer: Admin Удалить админом комментарий с id {} ", commentId);
        commentService.deleteByAdmin(commentId);
        log.info("MainServer: Admin Удален админом комментарий id {}", commentId);
    }

    //Получить все комментарии по событию
    @GetMapping("/events/{eventId}/comments")
    public List<CommentDto> getAllByEvent(@PathVariable Long eventId,
                                         @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("MainServer: Public Получить все комментарии по событию с id: {}", eventId);
        final Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));

        List<CommentDto> comments = commentService.getAllByEvent(eventId, pageable);
        log.info("MainServer: Public  Получен список всех комментариев по событию с id: {}.  " +
                "Результат = {}", eventId, comments);

        return comments;
    }

    //Получить комментарии по Id
    @GetMapping("/comments/{commentId}")
    public CommentDto getById(@PathVariable Long commentId) {
        log.info("MainServer: Public Получить комментарий по id: {}", commentId);

        CommentDto comment = commentService.getById(commentId);
        log.info("MainServer: Public  Получен комментарий с id: {}.  " +
                "Результат = {}", commentId, comment);

        return comment;
    }

    //Найти комментарии по содержанию
    @GetMapping("/comments/search")
    public List<CommentDto> getByText(@RequestParam String text,
                                @RequestParam(name = "from", defaultValue = "0") Integer from,
                                @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("MainServer: Public Поиск комментариев по тексту: {}", text);
        final Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));

        List<CommentDto> comments = commentService.getByText(text, pageable);
        log.info("MainServer: Public Поиск комментариев по тексту: {}.  " +
                "Результат = {}", text, comments);

        return comments;
    }
}
