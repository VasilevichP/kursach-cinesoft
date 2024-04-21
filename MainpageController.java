package com.example.Cinesoft.Controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainpageController {

    @GetMapping("/mainpage")
    public String mainpage(@ModelAttribute("user") String user, Model model, HttpSession session) {
        System.out.println("mainpage get");
        String us = (String) session.getAttribute("user");
        System.out.println("mainpage us:" +us);
        if (us!=null) {
            if ((us.equals("admin") || us.equals("user"))) {
                model.addAttribute("user", us);
                return "mainpage";
            }
        }
        return "redirect:/authorization";
    }

    @PostMapping("/mainpage/toRequest")
    public String toRequest(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("user", model.getAttribute("user"));
        return "redirect:/film_request";
    }
    @PostMapping("/mainpage/toCreate")
    public String toCreate(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        return "redirect:/session_creation";
    }
    @PostMapping("/mainpage/toSchedule")
    public String toSchedule(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        return "redirect:/schedule";
    }
    @PostMapping("/mainpage/toFilms")
    public String toFilms(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        return "redirect:/films";
    }
    @PostMapping("/mainpage/halls")
    public String toHalls(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        return "redirect:/halls";
    }
    @PostMapping("/mainpage/accounts")
    public String toAccounts(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        return "redirect:/accounts";
    }

    @PostMapping("/mainpage/back")
    public String back(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        session.removeAttribute("user");
        return "redirect:/authorization";
    }
}
