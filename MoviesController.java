package com.example.Cinesoft.Controllers;

import com.example.Cinesoft.Entities.Genres;
import com.example.Cinesoft.Entities.Movie;
import com.example.Cinesoft.Services.MovieService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.StreamSupport;

@Controller
public class MoviesController {
    @Autowired
    private MovieService movieService;

    @GetMapping("/films")
    public String get(Model model, HttpSession session) {
        System.out.println("films get");
        String user = (String) session.getAttribute("user");
        if (user != null) {
            if (user.equals("admin") || user.equals("user")) {
                movieService.deleteAllExpired(LocalDate.now());
                Iterable<Movie> movies = movieService.getAllMovies();
                if (StreamSupport.stream(movies.spliterator(), false).count() == 0)
                    model.addAttribute("error", "В базе данных кинотеатра нет фильмов");
                else {
                    if (!model.containsAttribute("movies")) model.addAttribute("movies", movies);
                    else movies = (Iterable<Movie>) model.getAttribute("movies");
                    if (StreamSupport.stream(movies.spliterator(), false).count() == 0)
                        model.addAttribute("error", "Не найдено фильмов по вашему запросу");
                    else {
                        ArrayList<String> genres = new ArrayList<String>();
                        for (Movie m : movies) {
                            genres.add(movieService.findGenres(m.getId()));
                        }
                        model.addAttribute("genres", genres);
                    }
                    if (session.getAttribute("filtValue") != null) {
                        System.out.println("f. v. :" + session.getAttribute("filtValue"));
                        model.addAttribute("filtValue", session.getAttribute("filtValue"));
                    }
                    if (session.getAttribute("sortValue") != null) {
                        System.out.println("s. v. :" + session.getAttribute("sortValue"));
                        model.addAttribute("sortValue", session.getAttribute("sortValue"));
                    }
                    ArrayList<String> distGenres = movieService.getDistinctGens();
                    model.addAttribute("distGenres", distGenres);
                }
                return "films";
            }
        }
        return "redirect:/authorization";
    }

    @PostMapping("/films/sort")
    public String sort(@RequestParam String sortBox, Model model, HttpSession session) {
        System.out.println(sortBox);
        Iterable<Movie> allMovies = movieService.getAllMovies();
        return "films";
    }

    @PostMapping("/films/filter")
    public String filter(@RequestParam String filtBox, Model model, HttpSession session) {
        System.out.println(filtBox);
        return "films";
    }

    @PostMapping("/films/search")
    public String search(@RequestParam String search, @RequestParam Optional<String> filtBox, @RequestParam Optional<String> sortBox, Model model, HttpSession session) {
        movieService.deleteAllExpired(LocalDate.now());
        Iterable<Movie> movies = movieService.getAllMovies();
        if (filtBox.isPresent()) {
            String filt = filtBox.get();
            movies = movieService.filter(movies, filt);
            session.removeAttribute("filtValue");
            session.setAttribute("filtValue", filt);
        }
        if (sortBox.isPresent()) {
            String sort = sortBox.get();
            movies = movieService.sort(movies, sort);
            session.removeAttribute("sortValue");
            session.setAttribute("sortValue", sort);
        } else session.setAttribute("sortValue", "");
        if (!(search == null || search == "")) {
            movies = movieService.search(movies, search);
            model.addAttribute("searchValue", search);
        }
        model.addAttribute("movies", movies);
        return get(model, session);
    }

    @GetMapping("/films/{id:\\d+}")
    public String toDetails(@PathVariable(value = "id") long id, Model model, HttpSession session) {
        Movie movie = movieService.getMovie(id);
        model.addAttribute("movie", movie);
        model.addAttribute("genres", movieService.findGenres(id));
        return "movie_details";
    }

    @GetMapping("/movie_details/delete/{id:\\d+}")
    public String delete(@PathVariable(value = "id") long id, Model model, HttpSession session) {
        String movie = movieService.getMovie(id).getName();
        movieService.deleteMovie(id);
        model.addAttribute("deletedMovie", movie);
        return get(model, session);
    }

    @GetMapping("/films/toFilms")
    public String toMovies(Model model, HttpSession session) {
        Iterable<Movie> movies = movieService.getAllMovies();
        if (session.getAttribute("sortValue") != null)
            movies = movieService.sort(movies, (String) session.getAttribute("sortValue"));
        if (session.getAttribute("filtValue") != null)
            movies = movieService.filter(movies, (String) session.getAttribute("filtValue"));
        model.addAttribute("movies", movies);
        return get(model, session);
    }

    @GetMapping({"/films/mainpage", "/movie_details/delete/mainpage"})
    public String toMainpage(Model model, HttpSession session) {
        session.removeAttribute("filtValue");
        session.removeAttribute("sortValue");
        return "redirect:/mainpage";
    }

    @GetMapping({"/films/authorization", "/movie_details/delete/authorization"})
    public String toAuthorization(Model model, HttpSession session) {
        session.removeAttribute("filtValue");
        session.removeAttribute("sortValue");
        return "redirect:/authorization";
    }

}