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
        UserInterface.login("safouat7@gmail.com", "1234567890").thenAccept(user -> {
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
        replaceFragment(getSupportFragmentManager(), new HomeFragment());

        // Set up bottom navigation view
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.itm_home) {
                // Perform action for Home item selection
                replaceFragment(getSupportFragmentManager(), new HomeFragment());
                Log.d("BottomNavigation", "Home item selected");
                return true;
            } else if (itemId == R.id.itm_add_users) {
                // Perform action for Add item selection
                replaceFragment(getSupportFragmentManager(), new UserFormFragment());
                return true;

            }else if (itemId == R.id.itm_add_plant) {
                // Perform action for Add item selection
                replaceFragment(getSupportFragmentManager(), new AddPlantFragment());
                return true;

            }  else if (itemId == R.id.itm_users) {
                // Perform action for Users item selection
                replaceFragment(getSupportFragmentManager(), new UsersFragment());
                return true;
            } else if (itemId == R.id.itm_profile) {
                // Perform action for Profile item selection
                replaceFragment(getSupportFragmentManager(), ProfileFragment.newInstance("UserName", "UserEmail"));
                Log.d("BottomNavigation", "Profile item selected");
                return true;
            } else {
                return false;
            }
        });
    }

    public static void replaceFragment(FragmentManager fragmentManager, Fragment fragment) {
        // Begin the transaction
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace the fragment
        transaction.replace(R.id.fragment_container, fragment);

        // Add the transaction to the back stack (optional)
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

}
