package com.example.Cinesoft.Repositories;

import com.example.Cinesoft.Entities.Account;
import com.example.Cinesoft.Entities.Movie;
import org.springframework.data.repository.CrudRepository;

public interface MovieRepository extends CrudRepository<Movie,Long> {
}
