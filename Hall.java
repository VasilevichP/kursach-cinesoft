package com.example.Cinesoft.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Hall {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int permission;
    private boolean status;
    private int places;

    public Hall(int permission, boolean status, int places) {
        this.permission = permission;
        this.status = status;
        this.places = places;
    }
}
