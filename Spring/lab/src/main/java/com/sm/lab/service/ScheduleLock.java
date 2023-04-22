package com.sm.lab.service;

import com.sm.lab.dto.ScheduleRequestDto;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ScheduleLock {

    private final ScheduleServiceV2 scheduleService;
    private final RedissonClient redissonClient;

    public void register(ScheduleRequestDto req) {
        LocalDateTime start = LocalDateTime.parse(req.start(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String date = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 00:00:00";

        String lockKey = req.title() + " : " + date;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(5, 3, TimeUnit.SECONDS);
            if (isLocked) {
                // 락 획득 후 수행할 작업들
                // 락 범위가 트랜잭션 범위보다 커야한다.
                scheduleService.register(req);
            } else {
                throw new RuntimeException("다시 시도해주세요.");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
