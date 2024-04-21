package com.example.Cinesoft.Controllers;

import com.example.Cinesoft.Entities.Movie;
import com.example.Cinesoft.Services.MovieService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;

@Controller
public class MovieRequestController {
    public static ArrayList<String> movieGenres = new ArrayList<String>();
    @Autowired
    private MovieService movieServices;


    @GetMapping("/film_request")
    public String get(Model model, HttpSession session) {
        System.out.println("req get");
        String us = (String) session.getAttribute("user");
        if (us != null) {
            if (us.equals("admin") || us.equals("user")) {
                return "film_request";
            }
        }
        return "redirect:/authorization";
    }

    @PostMapping("/film_request/add")
    public String add(@RequestParam String perm, @RequestParam String rentalPeriod, Model model, HttpSession session) {
        System.out.println("req add");
        System.out.println(perm);int per = Integer.parseInt(perm);
        System.out.println(rentalPeriod);int rent = Integer.parseInt(rentalPeriod);
        LocalDate localDate = LocalDate.now().plusDays(rent);
        System.out.println(localDate);
        Movie movie = (Movie) session.getAttribute("movie");
        ArrayList<String> genres = (ArrayList<String>) session.getAttribute("genres");
        System.out.println("req add movie: " + movie.getName());
        System.out.println("req add gens: ");
        movie.setPermission(per);movie.setDate_of_return(localDate);
        if (movieServices.addMovieToDB(movie)){
        for (String s : genres) movieServices.addGenreToDB(movie.getId(),s);
        model.addAttribute("success","Фильм "+movie.getName()+" был добавлен в базу данных");
        }else model.addAttribute("error","Возникла ошибка при добавлении фильма в базу данных");
        return get(model,session);
    }

    @PostMapping("/film_request/get_film")
    public String getFilm(@RequestParam String title, Model model, HttpSession session) {
        if (!movieGenres.isEmpty()) movieGenres.clear();
        if (title.isEmpty()) model.addAttribute("error", "Введите название фильма");
        else {
            Movie movie = movieServices.getMovie(title);
            if (movie.getId() == null) model.addAttribute("error", "Не найдено информации по запрашиваемому фильму");
            else if (movie.getMovieLength() == 0)
                model.addAttribute("error", "Данный фильм не предназначен для просмотра в кинотеатре");
            else if (movieServices.findById(movie.getId()))
                model.addAttribute("error", "Данный фильм уже есть в базе данных кинотеатра");
            else {
                if (!movie.getName().equals(title))
                    model.addAttribute("warning", "Возможно, вы имели ввиду этот фильм");
                String genres = "";
                StringBuilder stringBuilder = new StringBuilder(genres);
                for (String s : movieGenres) {stringBuilder.append(s + ", ");}
                genres = stringBuilder.toString();
                genres = genres.substring(0, genres.lastIndexOf(','));
                model.addAttribute("movie", movie);
                session.setAttribute("movie", movie);
                model.addAttribute("genres", genres);
                session.setAttribute("genres", movieGenres);
            }
        }
        return "film_request";
    }

    @GetMapping("/film_request/mainpage")
    public String toMainpage(Model model, HttpSession session) {
        session.removeAttribute("movie");
        session.removeAttribute("genres");
        return "redirect:/mainpage";
    }

    @GetMapping("/film_request/authorization")
    public String toAuthorization(Model model, HttpSession session) {
        session.removeAttribute("movie");
        session.removeAttribute("genres");
        return "redirect:/authorization";
    }
}
