package com.sm.lab.controller;

import com.sm.lab.dto.ScheduleRequestDto;
import com.sm.lab.service.ScheduleLock;
import com.sm.lab.service.ScheduleService;
import com.sm.lab.service.ScheduleServiceV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ScheduleController {
    private final ScheduleService service;
    private final ScheduleServiceV2 serviceV2;
    private final ScheduleLock scheduleLockService;

    @PostMapping
    public void createSchedule(@RequestBody ScheduleRequestDto scheduleRequestDto) throws InterruptedException {
        scheduleLockService.register(scheduleRequestDto);
    }
}
