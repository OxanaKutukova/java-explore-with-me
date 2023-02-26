package ru.practicum.ewm.service.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.user.dto.UserDto;
import ru.practicum.ewm.service.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    @Autowired
    private final UserService userService;

    //Получить список пользователей по идентификаторам
    @GetMapping("/admin/users")
    public List<UserDto> getAllByIds(@RequestParam(value = "ids", required = false) Long[] ids,
                                     @RequestParam(name = "from", defaultValue = "0") Integer from,
                                     @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("MainServer: Получить список пользователей по идентификаторам {}", ids);
        final Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));

        List<UserDto> allUsers = userService.getAll(ids, pageable);
        log.info("MainServer: Получен список пользователей по идентификаторам пользователей. Результат = {}", allUsers);

        return allUsers;
    }


    //Добавить пользователя
    @PostMapping("/admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("MainServer: Добавить пользователя = {}", userDto);
        UserDto resUserDto = userService.create(userDto);
        log.info("MainServer: Пользователь добавлен: {}", resUserDto);

        return resUserDto;
    }

    //Изменить пользователя
    @PatchMapping("/admin/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto update(@PathVariable Long userId, @Valid @RequestBody UserDto userDto) {
        log.info("MainServer: Обновить пользователя с Id: {} на {}", userId, userDto);
        UserDto resUserDto = userService.update(userId, userDto);
        log.info("MainServer: Обновлен пользователь с id = {}, следующими данными: {}", userId, resUserDto);

        return resUserDto;
    }

    //Удалить пользователя
    @DeleteMapping("/admin/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        log.info("MainServer: Удалить пользователя с Id = {}", userId);
        userService.delete(userId);
        log.info("MainServer: Удален пользователь с id = {}", userId);
    }
}
