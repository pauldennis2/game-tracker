package com.tiy.web;

import javax.persistence.*;

/**
 * Created by Paul Dennis on 1/10/2017.
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    int id;

    @Column(nullable = false, unique = true)
    String name;

    @Column(nullable = false)
    String password;

    public User() {
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getPassword () {
        return password;
    }
}
