package com.example.Cinesoft.Repositories;

import com.example.Cinesoft.Entities.Movie;
import com.example.Cinesoft.Entities.Session;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

public interface SessionRepository extends CrudRepository<Session,Long> {
    void deleteAllByStatus(int status);
    void deleteAllByHall(long hall);
    Iterable<Session> findAllByDateAndHall(LocalDate date,Long hall);
    Iterable<Session> findAllByDate(LocalDate date);
    Iterable<Session> findAllByStatus(int stat);
    int countSessionByMovie(Long movie);

}
