package com.example.Cinesoft.Controllers;

import com.example.Cinesoft.Entities.Hall;
import com.example.Cinesoft.Entities.Movie;
import com.example.Cinesoft.Entities.Session;
import com.example.Cinesoft.Services.HallService;
import com.example.Cinesoft.Services.MovieService;
import com.example.Cinesoft.Services.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Controller
public class ScheduleController {
    @Autowired
    private MovieService movieService;
    @Autowired
    private HallService hallService;
    @Autowired
    private SessionService sessionService;

    @GetMapping("/schedule")
    public String getSchedule(Model model, HttpSession session) {
        System.out.println("schedule get");
        String user = (String) session.getAttribute("user");
        if (user != null) {
            if (user.equals("admin") || user.equals("user")) {
                sessionService.markAsShown(LocalDate.now());
                ArrayList<LocalDate> dates = new ArrayList<>();
                ArrayList<LocalDate> allDates = new ArrayList<>();
                Iterable<Session> sessions = sessionService.getAllSessionsByStatus(0);
                Iterable<Session> filteredSessions = sessionService.getAllSessionsByStatus(0);
                for (int i = 0; i < 7; i++) {
                    LocalDate date = LocalDate.now().plusDays(i);
                    allDates.add(date);
                    dates.add(date);
                }
                if (model.containsAttribute("dateFilt")) {
                    if (!model.getAttribute("dateFilt").equals("")) {
                        for (LocalDate date : allDates) {
                            if (!date.toString().equals(model.getAttribute("dateFilt"))) dates.remove(date);
                        }
                        filteredSessions = sessionService.filterByDay(filteredSessions,LocalDate.parse((String)model.getAttribute("dateFilt")));
                    }
                }
                model.addAttribute("allDates", allDates);
                model.addAttribute("dates", dates);
                ArrayList<Hall> allHalls = hallService.findNotBlockedHalls();
                ArrayList<Hall> halls = hallService.findNotBlockedHalls();
                if (model.containsAttribute("hallFilt")) {
                    if (!model.getAttribute("hallFilt").equals("")) {
                        for (Hall hall : allHalls)
                            if (!hall.getId().toString().equals(model.getAttribute("hallFilt"))) halls.remove(hall);
                        filteredSessions = sessionService.filterByHall(filteredSessions,Long.parseLong((String) model.getAttribute("hallFilt")));
                    }
                }
                ArrayList<ArrayList<Object>> has = new ArrayList<>();
                for (Hall h:halls){
                    ArrayList<Object> ha = new ArrayList<>();
                    ha.add(h.getId());ha.add(h.getPermission());has.add(ha);
                }

                ArrayList<ArrayList<Object>> ses = new ArrayList<>();
                for (Session s:filteredSessions){
                    ArrayList<Object> ses1=new ArrayList<>();
                    ses1.add(s.getId());ses1.add(s.getHall());ses1.add(s.getDate().getYear());
                    ses1.add(s.getDate().getMonthValue());ses1.add(s.getDate().getDayOfMonth());
                    ses1.add(s.getStart_time().getHour());ses1.add(s.getStart_time().getMinute());
                    ses1.add(s.getEnd_time().getHour());ses1.add(s.getEnd_time().getMinute());ses1.add(s.getMovie());
                    ses.add(ses1);
                }
                movieService.deleteAllExpired(LocalDate.now());
                Iterable<Movie> movies = movieService.getAllMovies();
                ArrayList<ArrayList<Object>> movs = new ArrayList<>();
                for (Movie movie:movies){
                    ArrayList<Object> mov = new ArrayList<>();
                    mov.add(movie.getId());
                    mov.add(movie.getMovieLength()); mov.add(movie.getPermission());
                    mov.add(movie.getAgeRating());movs.add(mov);
                }
                System.out.println(ses);
                model.addAttribute("sessions",ses);
                model.addAttribute("allHalls", allHalls);
                //model.addAttribute("halls", halls);
                model.addAttribute("halls", has);
                model.addAttribute("movies",movies);
                model.addAttribute("movs",movs);
                return "schedule";
            }
        }
        return "redirect:/authorization";
    }

    @GetMapping("/session_creation")
    public String getSessionCreation(Model model, HttpSession session) {
        System.out.println("ses cr get");
        String user = (String) session.getAttribute("user");
        if (user != null) {
            if (user.equals("admin") || user.equals("user")) {
                sessionService.markAsShown(LocalDate.now());
                movieService.deleteAllExpired(LocalDate.now());
                Iterable<Movie> movies = movieService.getAllMovies();
                ArrayList<ArrayList<Object>> movs = new ArrayList<>();
                ArrayList<String>names=new ArrayList<>();
                for (Movie movie:movies){
                    ArrayList<Object> mov = new ArrayList<>();
                    mov.add(movie.getId());names.add(movie.getName());
                    mov.add(movie.getMovieLength()); mov.add(movie.getPermission());
                    mov.add(movie.getAgeRating());movs.add(mov);
                }
                if (StreamSupport.stream(movies.spliterator(), false).count() == 0)
                    model.addAttribute("error", "В базе данных кинотеатра нет фильмов");
                else {model.addAttribute("movies", movies);model.addAttribute("movs", movs);model.addAttribute("names",names);}
                ArrayList<LocalDate> dates = new ArrayList<>();
                for (int i = 0; i < 7; i++) {
                    dates.add(LocalDate.now().plusDays(i));
                }
                model.addAttribute("dates", dates);
                ArrayList<Hall> halls = hallService.findNotBlockedHalls();
                ArrayList<ArrayList<Object>> has = new ArrayList<>();
                for (Hall h:halls){
                    ArrayList<Object> ha = new ArrayList<>();
                    ha.add(h.getId());ha.add(h.getPermission());has.add(ha);
                }
                model.addAttribute("halls", has);
                Iterable<Session> sessions = sessionService.getAllSessionsByStatus(0);
                ArrayList<ArrayList<Object>> ses = new ArrayList<>();
                for (Session s:sessions){
                    ArrayList<Object> ses1=new ArrayList<>();
                    ses1.add(s.getId());ses1.add(s.getHall());ses1.add(s.getDate().getYear());
                    ses1.add(s.getDate().getMonthValue());ses1.add(s.getDate().getDayOfMonth());
                    ses1.add(s.getStart_time().getHour());ses1.add(s.getStart_time().getMinute());
                    ses1.add(s.getEnd_time().getHour());ses1.add(s.getEnd_time().getMinute());ses1.add(s.getMovie());
                    ses.add(ses1);
                }
                model.addAttribute("sessions",ses);
                return "session_creation";
            }
        }
        return "redirect:/authorization";
    }

    @GetMapping({"/session_creation/mainpage", "/schedule/mainpage"})
    public String toMainpage(Model model, HttpSession session) {
        session.removeAttribute("filtValue");
        session.removeAttribute("sortValue");
        return "redirect:/mainpage";
    }

    @GetMapping({"/session_creation/authorization", "/schedule/authorization"})
    public String toAuthorization(Model model, HttpSession session) {
        session.removeAttribute("filtValue");
        session.removeAttribute("sortValue");
        return "redirect:/authorization";
    }

    @PostMapping("/schedule/filter")
    public String search(@RequestParam Optional<String> dateFiltBox, @RequestParam Optional<String> hallFiltBox, Model model, HttpSession session) {
        if (dateFiltBox.isPresent()) {
            String date = dateFiltBox.get();
            model.addAttribute("dateFilt", date);
        }
        if (hallFiltBox.isPresent()) {
            String hall = hallFiltBox.get();
            model.addAttribute("hallFilt", hall);
        }
        return getSchedule(model, session);
    }
    @PostMapping("/session_creation/create")
    public String create(@RequestParam String sesDate, @RequestParam String sesStartTime,@RequestParam String sesMovie,@RequestParam String sesHall,@RequestParam String sesEndTime, Model model, HttpSession session) {
        int year = Integer.parseInt(sesDate.substring(0,4));int month = Integer.parseInt(sesDate.substring(5,7));
        int day = Integer.parseInt(sesDate.substring(8));
        int startH = Integer.parseInt(sesStartTime.substring(0,2));int startM = Integer.parseInt(sesStartTime.substring(3));
        int endH = Integer.parseInt(sesEndTime.substring(0,2));int endM = Integer.parseInt(sesEndTime.substring(3));
        LocalDate date = LocalDate.of(year,month,day);
        LocalTime startTime = LocalTime.of(startH,startM);LocalTime endTime = LocalTime.of(endH,endM);
        Long movId = Long.parseLong(sesMovie);
        Movie movie;
        try {
            movie =movieService.getMovie(movId);
        }
        catch (Exception e){
            movie = new Movie();
        }
        if (!movie.getDate_of_return().isAfter(date)) {model.addAttribute("createError","Срок проката фильма истечет к "+date);}
        else {
            Session ses = new Session(movId,Long.parseLong(sesHall),date,startTime,endTime);
            System.out.println(ses.getStart_time());
            sessionService.createSession(ses);
            ArrayList<Object> sess = new ArrayList<>();sess.add(ses.getId()); sess.add(ses.getHall());sess.add(ses.getDate());
            sess.add(ses.getMovie());sess.add(ses.getStart_time());sess.add(ses.getEnd_time());
            model.addAttribute("createdSession",sess);model.addAttribute("success","Сеанс создан");
        }
        return getSessionCreation(model, session);
    }

    @PostMapping("/schedule/edit")
    public String edit(@RequestParam String sesDate, @RequestParam String sesStartTime,@RequestParam String sesNumber,@RequestParam String sesHall,@RequestParam String sesEndTime, Model model, HttpSession session) {
        System.out.println("edit: ");
        Long sesNum = Long.parseLong(sesNumber); System.out.println("snum: "+ sesNum);
        Long hall = Long.parseLong(sesHall); System.out.println("shall: "+ hall);
        int year = Integer.parseInt(sesDate.substring(0,4));int month = Integer.parseInt(sesDate.substring(5,7));
        int day = Integer.parseInt(sesDate.substring(8));
        int startH = Integer.parseInt(sesStartTime.substring(0,2));int startM = Integer.parseInt(sesStartTime.substring(3));
        int endH = Integer.parseInt(sesEndTime.substring(0,2));int endM = Integer.parseInt(sesEndTime.substring(3));
        LocalDate date = LocalDate.of(year,month,day);System.out.println("sdate: "+ date);
        LocalTime startTime = LocalTime.of(startH,startM);System.out.println("ssttime: "+ startTime);
        LocalTime endTime = LocalTime.of(endH,endM);System.out.println("sendtime: "+ endTime);
        Session sesToChange = sessionService.getSessionById(sesNum);
        sesToChange.setHall(hall);sesToChange.setDate(date);sesToChange.setStart_time(startTime);sesToChange.setEnd_time(endTime);
        sessionService.updateSession(sesToChange);
        model.addAttribute("success","Сеанс №"+sesNum+" был изменен");
        return getSchedule(model, session);
    }
    @PostMapping("/schedule/delete")
    public String delete(@RequestParam String sesToDel,Model model, HttpSession session){
        System.out.println("std: "+sesToDel);
        sessionService.deleteSession(Long.parseLong(sesToDel));
        model.addAttribute("success","Сеанс №"+sesToDel+" был удален");
        return getSchedule(model,session);
    }
    @RequestMapping(value = "/schedule/generateReport", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<byte[]> downloadFile(Model model) {
        try {
            File pdfFile = sessionService.generateCheckFile();
            byte[] fileContent = Files.readAllBytes(pdfFile.toPath());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", pdfFile.getName());
            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("error");
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
