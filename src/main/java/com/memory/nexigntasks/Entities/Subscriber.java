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

    public String getNumber() {
        return number;
    }
}
