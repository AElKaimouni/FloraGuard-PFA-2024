package com.example.floraguard_pfa_2024;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.floraguard_pfa_2024.User.UserInterface;

public class UserViewHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public TextView email;
    public Button update, delete;
    private FragmentActivity activity; // Use FragmentActivity instead of Context

    public UserViewHolder(View itemView, FragmentActivity activity) {
        super(itemView);
        this.activity = activity;  // Correct the context assignment
        name = itemView.findViewById(R.id.name); // Ensure these IDs match your layout
        email = itemView.findViewById(R.id.Email);
        delete = itemView.findViewById(R.id.delete);


        delete.setOnClickListener(v -> deleteUser());
    }

    public void deleteUser() {
        UserInterface.findOne(email.getText().toString()).thenAccept(res -> {
            res.delete().thenAccept(v -> {
                Log.d("user-delete", "User deleted: " + res.getEmail());
            }).exceptionally(e -> {
                Log.e("user-delete", "Failed to delete user: " + e.getMessage());
                return null;
            });
        }).exceptionally(e -> {
            Log.e("user-delete", "Failed to find user: " + e.getMessage());
            return null;
        });
    }

    /*public void updateUser() {
        ProfileFragment profileFragment = ProfileFragment.newInstance(name.getText().toString(), email.getText().toString());
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, profileFragment)
                .addToBackStack(null)
                .commit();
    }*/
}
