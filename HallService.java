package com.example.Cinesoft.Services;

import com.example.Cinesoft.Entities.Hall;
import com.example.Cinesoft.Repositories.HallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class HallService {
    private final HallRepository hallRepository;

    @Autowired
    public HallService(HallRepository hallRepository) {
        this.hallRepository = hallRepository;
    }

    public boolean findHallById(Long id) {
        Optional<Hall> optHall = hallRepository.findById(id);
        if (optHall.isEmpty()) return false;
        else return true;
    }

    public Iterable<Hall> getAllHalls(){
        Iterable<Hall> halls =hallRepository.findAll();
        return halls;
    }
    public void changeStatus(long id){
        Hall hall = getHall(id);
        if (hall.isStatus()) hall.setStatus(false);
        else hall.setStatus(true);
        hallRepository.save(hall);
    }
    public boolean deleteById(long id){
        try {
            hallRepository.deleteById(id);return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public Hall getHall(Long id){
        Optional<Hall> optHall = hallRepository.findById(id);
        return optHall.get();
    }
    public boolean addHallToDB(Hall hall){
        try {
            hallRepository.save(hall);return true;
        }
        catch (Exception e){
            return false;
        }
    }
    public ArrayList<Hall> findNotBlockedHalls(){
        return (ArrayList<Hall>) hallRepository.findHallByStatus(true);
    }

}
