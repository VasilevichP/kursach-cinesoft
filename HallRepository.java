package com.example.Cinesoft.Repositories;

import com.example.Cinesoft.Entities.Hall;
import com.example.Cinesoft.Entities.Movie;
import org.springframework.data.repository.CrudRepository;

public interface HallRepository extends CrudRepository<Hall,Long> {
    Iterable<Hall> findHallByStatus(boolean stat);
}
