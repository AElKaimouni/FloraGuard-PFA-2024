package com.example.floraguard_pfa_2024;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.floraguard_pfa_2024.User.UserInterface;
import com.example.floraguard_pfa_2024.User.UserModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

// find user by email
        UserInterface.findOne("admin@floraguard.pfa").thenAccept(res -> {
            Log.d("user-find", res.getName());
        });

// login
        UserInterface.login("abdo@gmail.com", "1234567899").thenAccept(user -> {
            if(user == null) {
                Log.d("user-auth", "unvalid credentails");
            } else {
                Log.d("user-auth", user.getName());
            }
        });

// auth
        UserInterface.auth().thenAccept(user -> {
            if(user == null) {
                Log.d("user-auth", "unvalid session");
            } else {
                Log.d("user-auth", user.getName());
            }
        });

        UserInterface.create("abdo", "abdo@gmail.com", "12345678", "").whenComplete((res, exception) -> {
            if(exception == null) {
                Log.d("user-create", res.getName());
            } else { // if email is duplicated
                Log.d("user", exception.getMessage());
            }
        });

// all users
        UserInterface.all().thenAccept(res -> {
            for(UserModel user:res) {
                Log.d("user-table", user.getName());
            }
        });

// users count
        UserInterface.count().thenAccept(res -> {
            Log.d("user-count", String.valueOf(res));
        });

// update user
        UserInterface.findOne("abdo@gmail.com").thenAccept(res -> {
            // "1234567899" is the new password and its optional param
            res.setName("abdo334").setAvatar("avatar334").update("1234567899").thenAccept(v -> {
                Log.d("user-update", "user updated " + res.getEmail());
            });

        });

// delete user
        UserInterface.findOne("abdo@gmail.com").thenAccept(res -> {
            res.delete().thenAccept(v -> {
                Log.d("user-delete", "user deleted " + res.getEmail());
            });
        });

// logout
        UserInterface.logout();
    }
}