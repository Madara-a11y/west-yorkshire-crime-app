package com.example.westyorkshirecrimeapp;

public class User {
    public String name;
    public String email;
    public String role;

    // 1. Empty constructor (Required for Firebase)
    public User() {}

    // 2. Full constructor (Must have 3 arguments to match the variables)
    public User(String name, String email, String role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }
}