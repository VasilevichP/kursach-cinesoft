package com.example.Cinesoft.Controllers;

import com.example.Cinesoft.Entities.Hall;
import com.example.Cinesoft.Services.HallService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.stream.StreamSupport;

@Controller
public class HallsController {
    @Autowired
    private HallService hallService;

    @GetMapping("/halls")
    public String get(Model model, HttpSession session) {
        System.out.println("halls get");
        String us = (String) session.getAttribute("user");
        if (us != null) {
            if (us.equals("user") || us.equals("admin")) {
                Iterable<Hall> halls = hallService.getAllHalls();
                if (StreamSupport.stream(halls.spliterator(), false).count() == 0)
                    model.addAttribute("noHalls", "Список кинозалов пуст");
                else model.addAttribute("halls", halls);
                return "halls";
            }
        }
        return "redirect:/authorization";
    }

    @PostMapping("/halls/add")
    public String addHall(@RequestParam String perm, @RequestParam int places, Model model, HttpSession session) {
        System.out.println("perm: "+perm);int permission = Integer.parseInt(perm);
        System.out.println("places: "+places);
        Hall hall = new Hall(permission,true,places);
        hallService.addHallToDB(hall);
        model.addAttribute("success","Созданный зал был добавлен в базу данных");
        return get(model, session);
    }

    @GetMapping("/halls/status/{id}")
    public String changeStatus(@PathVariable(value = "id") long id, Model model, HttpSession session) {
        System.out.println("hall id:" + id);
        hallService.changeStatus(id);
        model.addAttribute("changed",id);
        return get(model, session);
    }
    @GetMapping("/halls/delete/{id}")
    public String delete(@PathVariable(value = "id") long id, Model model, HttpSession session) {
        System.out.println("hall id:" + id);
        hallService.deleteById(id);
        model.addAttribute("success","Зал " +id+ " был удален из базы данных");
        return get(model, session);
    }
    @GetMapping({"/halls/mainpage","/halls/status/mainpage","/halls/delete/mainpage"})
    public String toMainpage(Model model) {
        return "redirect:/mainpage";
    }

    @GetMapping({"/halls/authorization","/halls/status/authorization","/halls/delete/authorization"})
    public String toAuthorization(Model model) {
        return "redirect:/authorization";
    }
}
