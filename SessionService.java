package com.example.Cinesoft.Services;

import com.example.Cinesoft.Entities.Session;
import com.example.Cinesoft.Repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;
    @Autowired
    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Iterable<Session> getAllSessions(){
        Iterable<Session> sessions =sessionRepository.findAll();
        return sessions;
    }
    public boolean createSession(Session session){
        try {
            sessionRepository.save(session);return true;
        }
        catch (Exception e){
            return false;
        }
    }
    public Iterable<Session> findByDateAndHall(LocalDate date,Long id){
        return sessionRepository.findAllByDateAndHall(date,id);
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
    public void deleteShown(){
        sessionRepository.deleteAllByStatus(1);
    }
}
