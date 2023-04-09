package ru.practicum.ewm.service.comment.model;

import ru.practicum.ewm.service.comment.dto.CommentDto;
import ru.practicum.ewm.service.comment.dto.NewCommentDto;
import ru.practicum.ewm.service.event.model.EventMapper;
import ru.practicum.ewm.service.user.model.UserMapper;
import ru.practicum.ewm.service.utills.MainServiceDateTimeFormatter;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment toComment(NewCommentDto newCommentDto) {
        return Comment
                .builder()
                .id(newCommentDto.getId())
                .text(newCommentDto.getText())
                .insertDate(LocalDateTime.now())
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto
                .builder()
                .id(comment.getId())
                .text(comment.getText())
                .insertDate(MainServiceDateTimeFormatter.dateTimeToString(comment.getInsertDate()))
                .author(UserMapper.toUserShortDto(comment.getAuthor()))
                .event(EventMapper.toEventShortDto(comment.getEvent()))
                .build();
    }
}
