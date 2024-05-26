package com.example.floraguard_pfa_2024;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.floraguard_pfa_2024.User.UserInterface;

public class UserViewHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public TextView email;
    public ImageView image;
    public Button delete;
    private FragmentActivity activity;

    public UserViewHolder(View itemView, FragmentActivity activity) {
        super(itemView);
        this.activity = activity;
        name = itemView.findViewById(R.id.name);
        email = itemView.findViewById(R.id.Email);
        image=itemView.findViewById(R.id.image124);
        delete = itemView.findViewById(R.id.delete);


        delete.setOnClickListener(v -> deleteUser());
    }

    public void deleteUser() {
        UserInterface.findOne(email.getText().toString()).thenAccept(res -> {
            res.delete().thenAccept(v -> {
                Log.d("user-delete", "User deleted: " + res.getEmail());
                showToast("User deleted");
            }).exceptionally(e -> {
                Log.e("user-delete", "Failed to delete user: " + e.getMessage());
                showToast("Failed to delete user");
                return null;
            });
        }).exceptionally(e -> {
            Log.e("user-delete", "Failed to find user: " + e.getMessage());
            showToast("Failed to find user");
            return null;
        });
    }

    private void showToast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }
    private void goToMainActivity() {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        // If you want to remove this fragment from the back stack:
        activity.getSupportFragmentManager().popBackStack();
    }
}
