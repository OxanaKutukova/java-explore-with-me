package ru.practicum.ewm.service.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.service.user.dto.UserDto;

import java.util.List;

public interface UserService {


    UserDto create(UserDto userDto);

    UserDto update(Long userId, UserDto userDto);

    void delete(Long userId);

    List<UserDto> getAll(Long[] ids, Pageable pageable);
}
