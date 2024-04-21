package com.example.Cinesoft.Controllers;

import com.example.Cinesoft.Entities.Account;
import com.example.Cinesoft.Services.AccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthorizationController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/first_admin/add")
    public String firstAdminAdd(Model model) {
        System.out.println("auth.get");
        return "first_admin";
    }

    @GetMapping("/authorization")
    public String authorization(Model model, HttpSession session) {
        if(!accountService.doesAdminExist()) return "first_admin";
        session.removeAttribute("user");
//        model.addAttribute("us_check",session.getAttribute("user"));
        return "authorization";
    }

    @PostMapping("/first_admin/add")
    public String addAdmin(@RequestParam String adm_login, @RequestParam String adm_password, Model model) {
        if (adm_login.isEmpty() && adm_password.isEmpty()) model.addAttribute("error", "Введите логин и пароль");
        else if (adm_login.isEmpty()) model.addAttribute("error", "Введите логин");
        else if (adm_password.isEmpty()) {
            model.addAttribute("error", "Введите пароль");
            model.addAttribute("getLogin", adm_login);
        } else {
            switch (accountService.checkPassword(adm_password)) {
                case 1:
                    model.addAttribute("error", "Пароль должен быть не менее 8 символов");
                    model.addAttribute("getLogin", adm_login);
                    break;
                case 2:
                    model.addAttribute("error", "Пароль должен содержать хотя бы 1 символ верхнего регистра");
                    model.addAttribute("getLogin", adm_login);
                    break;
                case 3:
                    model.addAttribute("error", "Пароль должен содержать хотя бы одну цифру");
                    model.addAttribute("getLogin", adm_login);
                    break;
                case 0:
                    if (accountService.addAccountToDB(adm_login, adm_password, 1)) {
                        model.addAttribute("success", "Пользователь был добавлен в базу данных");return "redirect:/authorization";
                    } else model.addAttribute("error", "Возникла ошибка при добавлении учетной записи в базу данных");
                    break;
            }
        }
        return "first_admin";
    }

    @PostMapping("/authorization/authorize")
    public String authorize(@RequestParam String login, @RequestParam String password, Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        if (login == "") model.addAttribute("emptyLog", "Введите логин");
        else {
            if (password == "") {model.addAttribute("emptyPass", "Введите пароль");
                model.addAttribute("getLogin", login);}
            else {
                if (!accountService.findAccByLogin(login)) {model.addAttribute("error", "Неправильный логин или пароль");
                    model.addAttribute("getLogin", login);
                    model.addAttribute("getPassword", password);}
                else {
                    Account acc = accountService.getAcc(login);
                    if (!accountService.checkPassword(password, acc)){
                        model.addAttribute("error", "Неправильный логин или пароль");
                        model.addAttribute("getLogin", login);
                        model.addAttribute("getPassword", password);}
                    else {
                        if (acc.getRole() == 1) {
                            session.setAttribute("user", "admin");
                            redirectAttributes.addFlashAttribute("user", "admin");
                        } else {
                            session.setAttribute("user", "user");
                            redirectAttributes.addFlashAttribute("user", "user");
                        }
                        return "redirect:/mainpage";
                    }
                }
            }
        }
        return "authorization";
    }
}