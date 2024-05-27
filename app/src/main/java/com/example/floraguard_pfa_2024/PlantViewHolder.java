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
        plantName = itemView.findViewById(R.id.txt_plant_name); // Change here
        plantType = itemView.findViewById(R.id.txt_user); // Change here
        image2 = itemView.findViewById(R.id.img_plant);
        plantName.setOnClickListener(v -> goToPlantStateFragment());
    }

    private void goToPlantStateFragment() {
        // Get the plant name and image URL (if image2 is an ImageView, get the URL or resource ID)
        String plantNameText = plantName.getText().toString();
        String imageUrl = (String) image2.getTag(); // Assuming the image URL is stored as a tag

        // Create an instance of PlantStateFragment with the arguments
        PlantStateFragment fragment = PlantStateFragment.newInstance(plantNameText, imageUrl);

        // Replace the current fragment with PlantStateFragment
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment) // The container where the fragment will be replaced
                .addToBackStack(null) // Add to back stack to allow navigation back
                .commit();
    }
}
