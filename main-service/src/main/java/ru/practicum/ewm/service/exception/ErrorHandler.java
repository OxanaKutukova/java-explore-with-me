package ru.practicum.ewm.service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        log.error("404 {} ", e.getMessage(), e);

        return ApiError.builder()
                .status(ErrorStatus.NOT_FOUND)
                .message(e.getLocalizedMessage())
                .reason("Запрошены несуществующие данные")
                .build();
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, MissingServletRequestParameterException.class,
            BadRequestException.class, ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestExceptionList(final Exception e) {
        log.error("400 {} ", e.getMessage(), e);

        return ApiError.builder()
                .status(ErrorStatus.BAD_REQUEST)
                .message(e.getLocalizedMessage())
                .reason("Получены невалидные данные")
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbiddenException(final ForbiddenException e) {
        log.error("403 {} ", e.getMessage(), e);

        return ApiError.builder()
                .status(ErrorStatus.FORBIDDEN)
                .message(e.getLocalizedMessage())
                .reason("Отсутствуют права на доступ к данным")
                .build();
    }

    @ExceptionHandler({DataIntegrityViolationException.class, ConflictException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictRequestException(final Exception e) {
        log.info("409: {} ", e.getMessage(), e);

        return ApiError.builder()
                .status(ErrorStatus.CONFLICT)
                .message(e.getLocalizedMessage())
                .reason("В запросе невалидные значения для БД")
                .build();
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleHostUnreachableException(final HostUnreachableException e) {
        log.info("500: {} ", e.getMessage(), e);

        return ApiError.builder()
                .status(ErrorStatus.CONFLICT)
                .message(e.getLocalizedMessage())
                .reason("Нет сетевой доступности")
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(final Exception e) {
        log.error("500 {} ", e.getMessage(), e);
        String exceptionAsString = getStackTraceString(e);
        List<String> errors = new ArrayList<String>(Arrays.asList(exceptionAsString.split("\n")));

        return   ApiError.builder()
                .errors(errors)
                .status(ErrorStatus.INTERNAL_SERVER_ERROR)
                .message(e.getLocalizedMessage())
                .reason("Внутренняя ошибка сервера")
                .build();
    }

    public static String getStackTraceString(final Throwable e) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        e.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

}
