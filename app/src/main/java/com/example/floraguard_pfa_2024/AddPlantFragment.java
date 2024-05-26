package com.example.floraguard_pfa_2024;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.UUID;


import com.bumptech.glide.Glide;
import com.example.floraguard_pfa_2024.Plant.PlantInterface;
import com.example.floraguard_pfa_2024.Plant.PlantType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddPlantFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPlantFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private EditText plantName, plantType;
    private ImageView avatarPlant;
    private Button button, uploadAvatar;
    private Uri image1;
    private FirebaseAuth mAuth;
    private String email;

    public AddPlantFragment() {
        // Required empty public constructor
    }

    public static AddPlantFragment newInstance(String param1, String param2) {
        AddPlantFragment fragment = new AddPlantFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_plant, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            email = user.getEmail();
        }
        plantName = rootView.findViewById(R.id.ed_plant_name);
        plantType = rootView.findViewById(R.id.ed_plant_type);
        avatarPlant = rootView.findViewById(R.id.img_avatar);
        uploadAvatar = rootView.findViewById(R.id.btn_upload_avatar);
        button = rootView.findViewById(R.id.btn_save);

        uploadAvatar.setOnClickListener(v -> selectImage());
        button.setOnClickListener(v -> createPlant(image1));

        return rootView;
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                if (result.getData() != null) {
                    image1 = result.getData().getData();
                    Glide.with(AddPlantFragment.this).load(image1).into(avatarPlant);
                }
            } else {
                showToast("Please select an image");
            }
        }
    });


    private void createPlant(Uri imageUri) {
        if (imageUri != null) {
            // Generate a random ID for the plant
            String plantId = UUID.randomUUID().toString();

            // Upload the selected image to Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child("plants/" + plantId + ".jpg");

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            PlantInterface.create(plantName.getText().toString(), downloadUri.toString(), PlantType.TOMATO).thenAccept(plant -> {
                                Log.d("plant-create", "new plant created with id " + plant.getID());
                            });
                        }).addOnFailureListener(e -> {
                            Log.e("updateImage", "Failed to get download URL", e);
                            showToast("Failed to get download URL");
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("updateImage", "Image upload failed", e);
                        showToast("Image upload failed");
                    });
        } else {
            Log.e("updateImage", "Selected image URI is null");
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        startActivity(intent);
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
