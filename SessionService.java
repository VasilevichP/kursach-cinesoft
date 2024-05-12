package com.example.Cinesoft.Services;

import com.example.Cinesoft.Entities.Session;
import com.example.Cinesoft.Repositories.MovieRepository;
import com.example.Cinesoft.Repositories.SessionRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;
    private final MovieRepository movieRepository;
    @Autowired
    public SessionService(SessionRepository sessionRepository, MovieRepository movieRepository) {
        this.sessionRepository = sessionRepository;
        this.movieRepository = movieRepository;
    }


    public Iterable<Session> getAllSessionsByStatus(int stat){
        Iterable<Session> sessions =sessionRepository.findAllByStatus(stat);
        return sessions;
    }
    public boolean createSession(Session session){
        try {
            sessionRepository.save(session);System.out.println("true");
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
    public Session getSessionById(Long id){
        Optional<Session> optSession = sessionRepository.findById(id);
        return optSession.get();
    }
    public boolean updateSession(Session session){
        try {
            sessionRepository.save(session);return true;
        }
        catch (Exception e){
            return false;
        }
    }
    public boolean deleteSession (Long id){
        try {
            sessionRepository.deleteById(id);return true;
        }
        catch (Exception e){
            return false;
        }
    }
    public void markAsShown(LocalDate date){
        Iterable<Session> sessions = sessionRepository.findAll();
        for (Session s: sessions) if (s.getDate().isBefore(date)){
            s.setStatus(1);sessionRepository.save(s);
        }
    }
    public Iterable<Session> filterByDay(Iterable<Session> allSessions,LocalDate day){
        ArrayList<Session> sessions = (ArrayList<Session>) StreamSupport.stream(allSessions.spliterator(), false)
                .collect(Collectors.toList());
        Stream<Session> sess = sessions.stream();
        allSessions = sess.filter(s -> s.getDate().isEqual(day)).collect(Collectors.toList());
        System.out.println("fd: "+allSessions);
        return allSessions;
    }
    public Iterable<Session> filterByHall(Iterable<Session> allSessions,Long hall){
        ArrayList<Session> sessions = (ArrayList<Session>) StreamSupport.stream(allSessions.spliterator(), false)
                .collect(Collectors.toList());
        Stream<Session> sess = sessions.stream();
        allSessions = sess.filter(s -> s.getHall().equals(hall)).collect(Collectors.toList());
        System.out.println("fh: "+allSessions);
        return allSessions;
    }
    public File generateCheckFile() throws Exception {
        LocalDate startDate = LocalDate.now().minusDays(8);
        LocalDate endDate = LocalDate.now().minusDays(1);
        String fileName = "report_(" + startDate +"-"+endDate + ").pdf";
        File pdfFile = new File("./src/reports/" +fileName);
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
        document.open();
        BaseFont baseFont = BaseFont.createFont("./src/reports/Arial Cyr/Arial Cyr.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font = new Font(baseFont,12);
        Paragraph paragraph = new Paragraph("CINESOFT REPORT");
        paragraph.setAlignment(1);
        document.add(paragraph);
        for(int i=8;i>0;i--){
            LocalDate date = LocalDate.now().minusDays(i);
            document.add(new Paragraph("\n-----------------------------------------------------\n-----------------------------------------------------\n"));
            document.add(new Paragraph(date.toString()));
            ArrayList<Session> sess = (ArrayList<Session>) sessionRepository.findAllByDate(date);
            System.out.println(sess);
            document.add(new Paragraph("Общее количество сеансов: "+ sess.size(),font));
            int allMoviesShown = (int) sess.stream().filter(distinctByKey(Session::getMovie)).count();
            int allHallsUsed = (int) sess.stream().filter(distinctByKey(Session::getHall)).count();
            document.add(new Paragraph("Общее количество показанных фильмов: "+ allMoviesShown,font));
            document.add(new Paragraph("Общее количество использованных залов: "+ allHallsUsed,font));
           document.add(new Paragraph("\n-----------------------------------------------------\n"));
           for (Session s:sess){
               document.add(new Paragraph("\nСеанс № : "+ s.getId()+
                       "\nФильм: " +movieRepository.findById(s.getMovie()).get().getName()+
                       "\nЗал: "+ s.getHall()+
                       "\nВремя начала: "+s.getStart_time()+
                       "\nВремя конца: "+s.getEnd_time()+"\n",font));
           }
            document.add(new Paragraph("\n-----------------------------------------------------\n"));
       }

        document.close();
        return pdfFile;
    }
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
