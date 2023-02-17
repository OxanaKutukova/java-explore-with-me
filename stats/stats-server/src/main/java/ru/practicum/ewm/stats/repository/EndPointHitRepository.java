package ru.practicum.ewm.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.stats.model.EndPointHit;
import ru.practicum.ewm.stats.model.EndPointHitStats;


import java.time.LocalDateTime;
import java.util.List;

public interface EndPointHitRepository extends JpaRepository<EndPointHit, Long> {

    @Query("SELECT new ru.practicum.ewm.stats.model.EndPointHitStats(c.app, c.uri, COUNT(DISTINCT c.ip)) " +
            "FROM EndPointHit AS c  " +
            "WHERE c.timestamp BETWEEN ?1 AND ?2 AND c.uri IN(?3) " +
            "GROUP BY c.app, c.uri " +
            "ORDER BY COUNT(c.ip) DESC")
    List<EndPointHitStats> findAllByUrisAndUniqueIp(LocalDateTime startDateTime, LocalDateTime endDateTime, List<String> uris);

    @Query("SELECT new ru.practicum.ewm.stats.model.EndPointHitStats(c.app, c.uri, COUNT(DISTINCT c.ip)) " +
            "FROM EndPointHit AS c  " +
            "WHERE c.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY c.app, c.uri " +
            "ORDER BY COUNT(c.ip) DESC")
    List<EndPointHitStats> findAllByUniqueIp(LocalDateTime startDateTime, LocalDateTime endDateTime);

   @Query("SELECT new ru.practicum.ewm.stats.model.EndPointHitStats(c.app, c.uri, COUNT(c.ip)) " +
           "FROM EndPointHit AS c  " +
           "WHERE c.timestamp BETWEEN ?1 AND ?2 AND c.uri IN(?3) " +
           "GROUP BY c.app, c.uri " +
           "ORDER BY COUNT(c.ip) DESC")
   List<EndPointHitStats> findAllByTimestampBetweenAndUriIn(LocalDateTime startDateTime, LocalDateTime endDateTime, List<String> uris);

   @Query("SELECT new ru.practicum.ewm.stats.model.EndPointHitStats(c.app, c.uri, COUNT(c.ip)) " +
           "FROM EndPointHit AS c  " +
           "WHERE c.timestamp BETWEEN ?1 AND ?2 " +
           "GROUP BY c.app, c.uri " +
           "ORDER BY COUNT(c.ip) DESC")
   List<EndPointHitStats> findAllByTimestampBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);


}
