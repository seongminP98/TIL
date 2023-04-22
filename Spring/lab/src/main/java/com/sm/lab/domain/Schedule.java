package com.sm.lab.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;

    public Schedule(String title, LocalDateTime start, LocalDateTime end) {
        this.title = title;
        this.start = start;
        this.end = end;
    }
}
