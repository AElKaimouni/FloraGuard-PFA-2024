package com.example.floraguard_pfa_2024;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import com.example.floraguard_pfa_2024.User.UserInterface;
import com.example.floraguard_pfa_2024.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding; // Assuming you're using view binding

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Find user by email
        UserInterface.findOne("admin@floraguard.pfa").thenAccept(res -> {
            if (res != null) {
                Log.d("user-find", res.getName());
            } else {
                Log.d("user-find", "User not found");
            }
        }).exceptionally(e -> {
            Log.e("user-find", "Error finding user", e);
            return null;
        });

        // Login
        UserInterface.login("abdo@gmail.com", "1234567899").thenAccept(user -> {
            if (user == null) {
                Log.d("user-auth", "Invalid credentials");
            } else {
                Log.d("user-auth", user.getName());
            }
        }).exceptionally(e -> {
            Log.e("user-auth", "Error logging in", e);
            return null;
        });

        // Auth
        UserInterface.auth().thenAccept(user -> {
            if (user == null) {
                Log.d("user-auth", "Invalid session");
            } else {
                Log.d("user-auth", user.getName());
            }
        }).exceptionally(e -> {
            Log.e("user-auth", "Error authenticating", e);
            return null;
        });
        binding.bottomNavigationView.setOnItemReselectedListener(item -> {
            if (item.getItemId() == R.id.itm_home) {
                // Perform action for Home item reselection
                Log.d("BottomNavigation", "Home item reselected");
            } else if (item.getItemId() == R.id.itm_add_users) {
                // Perform action for Add item reselection
                replaceFragment(getSupportFragmentManager(),new UserFormFragment());

            } else if (item.getItemId() == R.id.itm_users) {
                // Perform action for Users item reselection
                replaceFragment(getSupportFragmentManager(),new UsersFragment());
            } else if (item.getItemId() == R.id.itm_profile) {
                replaceFragment(getSupportFragmentManager(),new ProfileFragment() );
                // Perform action for Profile item reselection
                Log.d("BottomNavigation", "Profile item reselected");
            }
        });
    }
    public static void replaceFragment(FragmentManager fragmentManager,Fragment fragment ) {
        // Begin the transaction

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Set arguments for the fragment if provided


        // Replace the fragment
        transaction.replace(R.id.fragment_container, fragment);

        // Add the transaction to the back stack (optional)
        // transaction.addToBackStack(tag);

        // Commit the transaction
        transaction.commit();
    }
    }
