package com.example.Cinesoft.Services;

import com.example.Cinesoft.Entities.Hall;
import com.example.Cinesoft.Entities.Session;
import com.example.Cinesoft.Repositories.HallRepository;
import com.example.Cinesoft.Repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class HallService {
    private final HallRepository hallRepository;
    private final SessionRepository sessionRepository;

    @Autowired
    public HallService(HallRepository hallRepository, SessionRepository sessionRepository) {
        this.hallRepository = hallRepository;
        this.sessionRepository = sessionRepository;
    }

    public Iterable<Hall> getAllHalls() {
        Iterable<Hall> halls = hallRepository.findAll();
        return halls;
    }

    public boolean changeStatus(long id) {
        try {
            Hall hall = getHall(id);
            if (hall.isStatus()) {
                hall.setStatus(false);
                deleteSessionByHall(id);
            } else hall.setStatus(true);
            hallRepository.save(hall);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteById(long id) {
        try {
            hallRepository.deleteById(id);
            deleteSessionByHall(id);
            return true;
        } catch (Exception e) {
            System.out.println("exception");
            return false;
        }
    }

    public void deleteSessionByHall(long id) {
        Iterable<Session> sessions = sessionRepository.findAllByHall(id);
        for (Session s : sessions) if (s.getStatus() == 0) sessionRepository.deleteById(s.getId());
    }

    public Hall getHall(Long id) throws Exception {
        Optional<Hall> optHall = hallRepository.findById(id);
        return optHall.get();
    }

    public boolean addHallToDB(Hall hall) {
        try {
            hallRepository.save(hall);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ArrayList<Hall> findNotBlockedHalls() {
        return (ArrayList<Hall>) hallRepository.findHallByStatus(true);
    }

}
