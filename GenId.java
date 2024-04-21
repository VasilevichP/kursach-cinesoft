package com.example.Cinesoft.Entities;

import java.io.Serializable;

public class GenId implements Serializable {
    private long movie;
    private String genre;

    public GenId() {
    }

    public GenId(long movie, String genre) {
        this.movie = movie;
        this.genre = genre;
    }
}
