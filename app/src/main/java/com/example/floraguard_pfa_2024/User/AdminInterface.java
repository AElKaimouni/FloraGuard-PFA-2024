package com.example.floraguard_pfa_2024.User;

public interface AdminInterface extends UserInterface {
    static UserModel login(String email, String password) {

        return  new UserModel("", "", "");
    }
}
