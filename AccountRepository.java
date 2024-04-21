package com.example.Cinesoft.Repositories;

import com.example.Cinesoft.Entities.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository <Account,String> {
    boolean existsByRole(int role);
}
