package com.example.Cinesoft.Controllers;

import com.example.Cinesoft.Entities.Account;
import com.example.Cinesoft.Services.AccountService;
import jakarta.servlet.http.HttpSession;
import org.hibernate.dialect.unique.CreateTableUniqueDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.stream.StreamSupport;

@Controller
public class AccountsController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/accounts")
    public String get(Model model, HttpSession session) {
        System.out.println("accs get");
        String us = (String) session.getAttribute("user");
        if (us != null) {
            if (us.equals("user")) return "mainpage";
            if (us.equals("admin")) {
                Iterable<Account> accounts = accountService.getAllAccounts();
                if (StreamSupport.stream(accounts.spliterator(), false).count() == 0)
                    model.addAttribute("noAccs", "Список учетных записей пуст");
                else model.addAttribute("accounts", accounts);
                return "accounts";
            }
        }
        return "redirect:/authorization";
    }

    @PostMapping("/accounts/add")
    public String addAccount(@RequestParam String login, @RequestParam String password, Model model, HttpSession session) {
        if (login.isEmpty() && password.isEmpty()) model.addAttribute("error", "Введите логин и пароль");
        else if (login.isEmpty()) model.addAttribute("error", "Введите логин");
        else if (password.isEmpty()) {
            model.addAttribute("error", "Введите пароль");
            model.addAttribute("getLogin", login);
        } else if (accountService.findAccByLogin(login)) {
            model.addAttribute("error", "Учетная запись с таким логином уже есть");
            model.addAttribute("getLogin", login);
        } else {
            switch (accountService.checkPassword(password)) {
                case 1:
                    model.addAttribute("error", "Пароль должен быть не менее 8 символов");
                    model.addAttribute("getLogin", login);
                    break;
                case 2:
                    model.addAttribute("error", "Пароль должен содержать хотя бы 1 символ верхнего регистра");
                    model.addAttribute("getLogin", login);
                    break;
                case 3:
                    model.addAttribute("error", "Пароль должен содержать хотя бы одну цифру");
                    model.addAttribute("getLogin", login);
                    break;
                case 0:
                    if (accountService.addAccountToDB(login, password, 0))
                        model.addAttribute("success", "Пользователь был добавлен в базу данных");
                    else model.addAttribute("error", "Возникла ошибка при добавлении учетной записи в базу данных");
                    break;
            }
        }
        return get(model, session);
    }

    @GetMapping("/accounts/delete/{login}")
    public String delete(@PathVariable(value = "login") String login, Model model, HttpSession session) {
        System.out.println("login to delete:" + login);
        if (accountService.getAcc(login).getRole() == 1) model.addAttribute("delAdmin", login);
        else {
            accountService.deleteAccountByLogin(login);
            model.addAttribute("deleted", login);
        }
        return get(model, session);
    }

    @GetMapping({"/accounts/mainpage","/accounts/delete/mainpage"})
    public String toMainpage(Model model) {
        return "redirect:/mainpage";
    }

    @GetMapping({"/accounts/authorization","/accounts/delete/authorization"})
    public String toAuthorization(Model model) {
        return "redirect:/mainpage";
    }

}
