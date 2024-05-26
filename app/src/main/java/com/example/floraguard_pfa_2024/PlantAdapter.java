package com.example.floraguard_pfa_2024;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.floraguard_pfa_2024.Plant.PlantModel;
import com.example.floraguard_pfa_2024.User.UserModel;

import java.util.LinkedList;

public class PlantAdapter extends RecyclerView.Adapter<PlantViewHolder> {
    private LinkedList<PlantModel> plants;
    private Context context;

    public PlantAdapter(LinkedList<PlantModel> plants, Context context) {
        this.plants = plants;
        this.context = context;
    }

    @Override
    public PlantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_plant_layout, parent, false);
        return new PlantViewHolder(itemView, (FragmentActivity) parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        PlantModel plant = plants.get(position);
        holder.plantName.setText(plant.getName());
        if (plant.getImage() != null && !plant.getImage().isEmpty()) {
            Glide.with(context).load(plant.getImage()).into(holder.image2);
        } else {
            holder.image2.setImageResource(R.drawable.baseline_add_24); // Set a placeholder image
        }
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }
}
