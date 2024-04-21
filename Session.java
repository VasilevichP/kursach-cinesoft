package com.example.Cinesoft.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long movie;
    private Long hall;
    private LocalDate date;
    private LocalTime start_time;
    private LocalTime end_time;
    public int status;
    public Session(Long movie_id, Long hall_id, LocalDate date, LocalTime start_time, LocalTime end_time) {
        this.movie = movie_id;
        this.hall = hall_id;
        this.date = date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.status=0;
    }
}
