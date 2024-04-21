package com.example.Cinesoft.Services;

import com.example.Cinesoft.Repositories.AccountRepository;
import com.example.Cinesoft.Entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountsRepository;

    @Autowired
    public AccountService(AccountRepository accountsRepository) {
        this.accountsRepository = accountsRepository;
    }

    public Iterable<Account> getAllAccounts() {
        return accountsRepository.findAll();
    }

    public boolean addAccountToDB(String login, String password, int role) {
        try {
            String salt = BCrypt.gensalt();
            String hashedPassword = BCrypt.hashpw(password, salt);
            Account acc = new Account(login,hashedPassword,role);
            accountsRepository.save(acc);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public int checkPassword(String password){
        if(password.length()<8) return 1;
        boolean uppers=false,num=false;
        for (int k = 0;k<password.length();k++){
            if (Character.isUpperCase(password.charAt(k))) uppers=true;
            if (Character.isDigit(password.charAt(k))) num=true;
        }
        if (uppers==false) return 2;
        if (num==false) return 3;
        return 0;
    }
    public boolean findAccByLogin(String login) {
        Optional<Account> optAcc = accountsRepository.findById(login);
        if (optAcc.isEmpty()) return false;
        else return true;
    }

    public Iterable<Account> getAllAccs(){
        Iterable<Account> accounts =accountsRepository.findAll();
        return accounts;
    }
    public boolean doesAdminExist() {
        return accountsRepository.existsByRole(1);
    }
    public Account getAcc(String login){
        Optional<Account> optAcc = accountsRepository.findById(login);
        return optAcc.get();
    }
    public boolean isAcc(String login){
        Optional<Account> optAcc = accountsRepository.findById(login);
        return optAcc.isPresent();
    }

    public boolean checkPassword(String password,Account acc){
        if (BCrypt.checkpw(password,acc.getPassword())) return true;
        else return false;
    }
    public void deleteAccountByLogin(String login) {
        accountsRepository.deleteById(login);
    }
}
