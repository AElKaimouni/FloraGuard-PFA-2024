package com.example.floraguard_pfa_2024;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class PlantViewHolder extends RecyclerView.ViewHolder {
    public TextView plantName;
    public TextView plantType;
    public ImageView image2;
    public Button delete;
    private FragmentActivity activity;

    public PlantViewHolder(View itemView, FragmentActivity activity) {
        super(itemView);
        this.activity = activity;
        plantName = itemView.findViewById(R.id.txt_plant_name); // Change her
        plantType = itemView.findViewById(R.id.txt_user); // Change here
        image2=itemView.findViewById(R.id.img_plant);
    }
}
