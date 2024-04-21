package com.example.Cinesoft.Repositories;

import com.example.Cinesoft.Entities.Account;
import com.example.Cinesoft.Entities.GenId;
import com.example.Cinesoft.Entities.Genres;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface GenresRepository extends CrudRepository<Genres, GenId> {
    ArrayList<Genres> findByMovie(long id);
}
