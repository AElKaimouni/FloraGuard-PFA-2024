package com.example.floraguard_pfa_2024;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.floraguard_pfa_2024.User.UserInterface;
import com.example.floraguard_pfa_2024.User.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private EditText name, password, Newpassword;
    private Button buttonSaveName, buttonPassword, buttonUploadImage, btnSaveAvatar;
    private ImageView image;
    private FirebaseAuth mAuth;
    private String email;
    private Uri image1;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String name, String email) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, name);
        args.putString(ARG_PARAM2, email);
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
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            email = user.getEmail();
        }

        name = rootView.findViewById(R.id.ed_name);
        name.setText(ProfileFragment.ARG_PARAM1);
        buttonSaveName = rootView.findViewById(R.id.btn_save_name);
        buttonSaveName.setOnClickListener(v -> updateName());

        password = rootView.findViewById(R.id.ed_new_password);
        Newpassword = rootView.findViewById(R.id.ed_confirm_password);
        buttonPassword = rootView.findViewById(R.id.btn_save_password);
        buttonPassword.setOnClickListener(v -> updatePassword());

        image = rootView.findViewById(R.id.img_avatar);
        buttonUploadImage = rootView.findViewById(R.id.btn_upload_avatar);
        buttonUploadImage.setOnClickListener(v->{selectImage();});
        btnSaveAvatar = rootView.findViewById(R.id.btn_save_avatar);
        btnSaveAvatar.setOnClickListener(v -> updateImage(image1));

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
            if (result.getResultCode() == Activity.RESULT_OK) { // Use Activity.RESULT_OK
                if (result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    image1 = selectedImageUri; // Assign the selected image URI to image1
                    Glide.with(ProfileFragment.this).load(image1).into(image); // Load the selected image into the ImageView
                }
            } else {
                showToast("Please select an image");
            }
        }
    });

    private void updateImage(Uri imageUri) {
        if (imageUri != null) {
            // Upload the selected image to Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child("avatars/" + email + ".jpg");

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully, get the download URL
                        imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            // Update the user's avatar with the download URL
                            UserInterface.findOne(email).thenAccept(user -> {
                                user.setAvatar(downloadUri.toString()).update().thenAccept(v -> {
                                    Log.d("user-update", "User updated with avatar URL: " + downloadUri);
                                    showToast("User updated");

                                    goToMainActivity();
                                });
                            });
                        }).addOnFailureListener(e -> {
                            Log.e("updateImage", "Failed to get download URL", e);
                            showToast("Failed to get download URL");
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("updateImage", "Image upload failed", e);
                        showToast("Image uploaded failed");
                    });
        } else {
            Log.e("updateImage", "Selected image URI is null");
        }
    }



    private void updatePassword() {
        if (password.getText().toString().equals(Newpassword.getText().toString())) {
            UserInterface.findOne(email).thenAccept(res -> {
                res.update(Newpassword.getText().toString()).thenAccept(v -> {
                    Log.d("user-update", "user updated " + res.getEmail());
                    showToast("user updated");

                    goToMainActivity();
                });
            });
        }
    }


    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }



    private void updateName() {
        if (!name.getText().toString().isEmpty()) {
            UserInterface.findOne(email).thenAccept(res -> {
                res.setName(name.getText().toString()).update().thenAccept(v -> {
                    Log.d("user-update", "user updated " + res.getEmail());
                    showToast("user updated");
                    goToMainActivity();
                });
            });
        }
    }
    private void goToMainActivity() {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        startActivity(intent);
        // If you want to remove this fragment from the back stack:
        requireActivity().getSupportFragmentManager().popBackStack();
    }

}
