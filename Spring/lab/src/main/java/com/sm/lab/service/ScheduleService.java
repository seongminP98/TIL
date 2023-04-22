package com.sm.lab.service;

import com.sm.lab.domain.Schedule;
import com.sm.lab.dto.ScheduleRequestDto;
import com.sm.lab.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Transactional
    public Long register(ScheduleRequestDto req) {
        LocalDateTime start = LocalDateTime.parse(req.start(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime end = LocalDateTime.parse(req.end(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String date = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 00:00:00";
        LocalDateTime from = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime to = from.plusDays(1);

        List<Schedule> result = scheduleRepository.findAllByStartBetweenAndTitle(from, to, req.title());
        result.forEach(r -> {
            if (r.getStart().equals(start) && r.getEnd().equals(end)) {
                throw new RuntimeException("중복입니다.");
            }
        });
        return scheduleRepository.save(new Schedule(req.title(), start, end)).getId();
    }
}
