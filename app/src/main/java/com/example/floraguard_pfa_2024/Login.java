package com.example.floraguard_pfa_2024;

import static com.example.floraguard_pfa_2024.User.UserInterface.login;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

public class Login extends AppCompatActivity {
    private EditText Email;
    private EditText Password;
    private Button button;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Email=findViewById(R.id.ed_email);
        Password=findViewById(R.id.ed_password);
        button=findViewById(R.id.btn_login);
        button.setOnClickListener(v->{login(Email.getText().toString(),Password.getText().toString()).thenAccept(user -> {
            if(user == null) {
                Log.d("user-auth", "unvalid credentails");
            } else {
                Log.d("user-auth", user.getName());
            }
        });
            ;});

    }

}