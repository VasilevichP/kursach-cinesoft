package com.example.Cinesoft.Controllers;

import com.example.Cinesoft.Repositories.AccountRepository;
import com.example.Cinesoft.Services.AccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/")
    public String greeting(Model model, HttpSession session) {
        System.out.println("main get");
        session.removeAttribute("user");
        session.removeAttribute("filtValue");
        session.removeAttribute("sortValue");
        model.addAttribute("us_check",session.getAttribute("user"));
        if(accountService.doesAdminExist()) return "authorization";
        else return "first_admin";
    }
}

