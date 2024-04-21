package com.example.Cinesoft.Repositories;

import com.example.Cinesoft.Entities.Session;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

public interface SessionRepository extends CrudRepository<Session,Long> {
    void deleteAllByStatus(int status);
    Iterable<Session> findAllByDateAndHall(LocalDate date,Long hall);
}
