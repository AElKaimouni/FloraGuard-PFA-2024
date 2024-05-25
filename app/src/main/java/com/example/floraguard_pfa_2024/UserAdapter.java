package com.example.floraguard_pfa_2024;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.floraguard_pfa_2024.User.UserModel;

import java.util.LinkedList;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {
    private LinkedList<UserModel> users;
    private Context context;

    // Provide a suitable constructor (depends on the kind of dataset)
    public UserAdapter(LinkedList<UserModel> users, Context context) {
        this.users = new LinkedList<>();
        this.users.addAll(users);
        this.context = context;
    }

    // Create new views (invoked by the layout manager)

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_user_layout, parent, false);
        return new UserViewHolder(itemView, (FragmentActivity) parent.getContext());
    }


    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        // Bind data to the views of the ViewHolder here
        UserModel user = users.get(position);
        holder.name.setText(user.getName());
        holder.email.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
