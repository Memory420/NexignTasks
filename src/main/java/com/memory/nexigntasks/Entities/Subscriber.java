package com.memory.nexigntasks.Entities;

import jakarta.persistence.*;

@Entity
public class Subscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String number;

    public Subscriber(String number) {
        this.number = number;
    }

    public Subscriber() {
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id; // для тестов
    }

    public String getNumber() {
        return number;
    }
}
