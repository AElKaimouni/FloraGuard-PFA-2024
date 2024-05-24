package com.example.floraguard_pfa_2024.User;

public class Admin extends UserModel implements AdminInterface {

    public Admin(String name, String email, String avatar) {
        super(name, email, avatar);
    }
}
