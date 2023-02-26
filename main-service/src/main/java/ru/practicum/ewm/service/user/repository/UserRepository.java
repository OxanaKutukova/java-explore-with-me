package ru.practicum.ewm.service.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.service.user.model.User;


import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT u FROM User u WHERE u.id IN ?1")
    List<User> findAllInId(List<Long> ids, Pageable pageable);
}
