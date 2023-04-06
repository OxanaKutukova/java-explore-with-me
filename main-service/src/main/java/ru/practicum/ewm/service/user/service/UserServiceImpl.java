package ru.practicum.ewm.service.user.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.exception.NotFoundException;
import ru.practicum.ewm.service.user.dto.UserDto;
import ru.practicum.ewm.service.user.model.User;
import ru.practicum.ewm.service.user.model.UserMapper;
import ru.practicum.ewm.service.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll(Long[] ids, Pageable pageable) {
        List<User> users;
        users = (ids == null)
                ? userRepository.findAll(pageable).getContent()
                : userRepository.findByIdIn(List.of(ids), pageable);

        return users
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }


    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        final User user = UserMapper.toUser(userDto);
        final User userS = userRepository.save(user);

        return UserMapper.toUserDto(userS);
    }

    @Transactional
    @Override
    public UserDto update(Long userId, UserDto userDto) {
        final User user = UserMapper.toUser(userDto);
        final User userU = getUserById(userId);
        if (user.getName() != null) {
            userU.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userU.setEmail(user.getEmail());
        }
        final User userSaved = userRepository.save(userU);

        return UserMapper.toUserDto(userSaved);
    }

    @Transactional
    @Override
    public void delete(Long userId) {
        throwIfNotExistUser(userId);
        userRepository.deleteById(userId);
    }

    private User getUserById(Long userId) {
        return  userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден или недоступен"));
    }

    private void throwIfNotExistUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден или недоступен"));
    }
}
