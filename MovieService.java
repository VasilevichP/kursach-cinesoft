package com.example.Cinesoft.Services;

import com.example.Cinesoft.Entities.Genres;
import com.example.Cinesoft.Entities.Movie;
import com.example.Cinesoft.Repositories.GenresRepository;
import com.example.Cinesoft.Repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.SocketException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.example.Cinesoft.CinesoftApplication.context;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final GenresRepository genresRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository, GenresRepository genresRepository) {
        this.movieRepository = movieRepository;
        this.genresRepository = genresRepository;
    }

    public Movie getMovie(String title) {
        ApiService apiService = context.getBean(ApiService.class);
        Movie movie;
        try {
            movie = apiService.getMovie(title);
        } catch (SocketException e) {
            movie = null;
        }
        return movie;
    }

    public boolean findById(Long id) {
        Optional<Movie> optMov = movieRepository.findById(id);
        if (optMov.isEmpty()) return false;
        else return true;
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    public Iterable<Movie> getAllMovies() {
        Iterable<Movie> movies = movieRepository.findAll();
        return movies;
    }

    public Movie getMovie(Long id) {
        Optional<Movie> optMovie = movieRepository.findById(id);
        return optMovie.get();
    }

    public boolean addMovieToDB(Movie movie) {
        try {
            movieRepository.save(movie);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String findGenres(long id) {
        String genres = "";
        ArrayList<Genres> genre = genresRepository.findByMovie(id);
        StringBuilder stringBuilder = new StringBuilder(genres);
        for (Genres s : genre) {
            stringBuilder.append(s.getGenre() + ", ");
        }
        genres = stringBuilder.toString();
        genres = genres.substring(0, genres.lastIndexOf(','));
        return genres;
    }

    public ArrayList<String> findArrGenres(long id) {
        ArrayList<Genres> genre = genresRepository.findByMovie(id);
        ArrayList<String> gens = new ArrayList<>();
        for (Genres g : genre) gens.add(g.getGenre());
        return gens;
    }

    public void addGenreToDB(long movie, String genre) {
        Genres gen = new Genres(movie, genre);
        genresRepository.save(gen);
    }

    public ArrayList<String> getDistinctGens() {
        Iterable<Genres> genres = genresRepository.findAll();
        ArrayList<String> gens = new ArrayList<>();
        Set<String> set = new HashSet<>();
        for (Genres g : genres) set.add(g.getGenre());
        gens.addAll(set);
        return gens;
    }

    public Iterable<Movie> search(Iterable<Movie> allMovies, String search) {
        ArrayList<Movie> searched = new ArrayList<Movie>();
        for (Movie m : allMovies) {
            if (m.getName().toLowerCase().contains(search.toLowerCase().strip())) {
                searched.add(m);
            }
        }
        return searched;
    }

    public void deleteOneExpired(Movie movie, LocalDate localDate) {
        if (!movie.getDate_of_return().isAfter(localDate)) movieRepository.delete(movie);
    }

    public void deleteAllExpired(LocalDate localDate) {
        Iterable<Movie> movies = movieRepository.findAll();
        for (Movie movie : movies) if (!movie.getDate_of_return().isAfter(localDate)) movieRepository.delete(movie);
    }

    public boolean checkOneExpired(Movie movie, LocalDate localDate) {
        return movie.getDate_of_return().isAfter(localDate);
    }

    public Iterable<Movie> sort(Iterable<Movie> allMovies, String sort) {
        ArrayList<Movie> movies = (ArrayList<Movie>) StreamSupport.stream(allMovies.spliterator(), false)
                .collect(Collectors.toList());
        Stream<Movie> movs = movies.stream();
        switch (sort) {
            case "1":
                allMovies = movs.sorted(Comparator.comparing(Movie::getName)).collect(Collectors.toList());
                break;
            case "2":
                allMovies = movs.sorted(Comparator.comparing(Movie::getName).reversed()).collect(Collectors.toList());
                break;
            case "3":
                allMovies = movs.sorted(Comparator.comparing(Movie::getMovieLength)).collect(Collectors.toList());
                break;
            case "4":
                allMovies = movs.sorted(Comparator.comparing(Movie::getMovieLength).reversed()).collect(Collectors.toList());
                break;
            case "5":
                allMovies = movs.sorted(Comparator.comparing(Movie::getDate_of_return)).collect(Collectors.toList());
                break;
            case "6":
                allMovies = movs.sorted(Comparator.comparing(Movie::getDate_of_return).reversed()).collect(Collectors.toList());
                break;
            default:
                break;
        }
        return allMovies;
    }

    public Iterable<Movie> filter(Iterable<Movie> allMovies, String filt) {
        ArrayList<Movie> movies = (ArrayList<Movie>) StreamSupport.stream(allMovies.spliterator(), false)
                .collect(Collectors.toList());
        Stream<Movie> movs = movies.stream();
        switch (filt) {
            case "-":
                return movies;
            case "2", "3", "5", "7":
                allMovies = movs.filter(m -> m.getPermission() == Integer.parseInt(filt)).collect(Collectors.toList());
                return allMovies;
            case "0", "6", "12", "18":
                allMovies = movs.filter(m -> m.getAgeRating() == Integer.parseInt(filt)).collect(Collectors.toList());
                return allMovies;
            default:
                System.out.println(filt);
                ArrayList<Movie> deleted = new ArrayList<>();
                for (Movie m : movies) {
                    if (!(findArrGenres(m.getId())).contains(filt)) {
                        deleted.add(m);
                    }
                }
                movies.removeAll(deleted);
        }
        return movies;
    }

}
