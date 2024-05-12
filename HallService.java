package com.example.Cinesoft.Services;

import com.example.Cinesoft.Entities.Hall;
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

    public void changeStatus(long id) {
        try{
            Hall hall = getHall(id);
            if (hall.isStatus()) {
                hall.setStatus(false);
                sessionRepository.deleteAllByHall(id);
            } else hall.setStatus(true);
            hallRepository.save(hall);
        }
        catch (Exception e){

        }
    }

    public boolean deleteById(long id) {
        try {
            hallRepository.deleteById(id);
            sessionRepository.deleteAllByHall(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Hall getHall(Long id) throws Exception{
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
