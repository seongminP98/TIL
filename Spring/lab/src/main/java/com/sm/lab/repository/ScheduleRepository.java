package com.sm.lab.repository;

import com.sm.lab.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Schedule s WHERE s.title = :title AND s.start BETWEEN :from AND :to")
    List<Schedule> findAllByStartBetweenAndTitle(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, @Param("title") String title);
}
