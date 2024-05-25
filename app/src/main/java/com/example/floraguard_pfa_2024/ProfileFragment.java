package com.example.floraguard_pfa_2024;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import com.example.floraguard_pfa_2024.User.UserInterface;
import com.example.floraguard_pfa_2024.User.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            email = user.getEmail();
        }

        name = rootView.findViewById(R.id.ed_name);
        buttonSaveName = rootView.findViewById(R.id.btn_save_name);
        buttonSaveName.setOnClickListener(v -> updateName());

        password = rootView.findViewById(R.id.ed_new_password);
        Newpassword = rootView.findViewById(R.id.ed_confirm_password);
        buttonPassword = rootView.findViewById(R.id.btn_save_password);
        buttonPassword.setOnClickListener(v -> updatePassword());

        image = rootView.findViewById(R.id.img_avatar);
        buttonUploadImage = rootView.findViewById(R.id.btn_upload_avatar);
        btnSaveAvatar = rootView.findViewById(R.id.btn_save_avatar);
        btnSaveAvatar.setOnClickListener(v -> updateImage());

        return rootView;
    }

    private void updateImage() {
        if (image.getDrawable() != null) {
            UserInterface.findOne(email).thenAccept(res -> {
                res.setAvatar(image.toString()).update().thenAccept(v -> {
                    Log.d("user-update", "user updated " + res.getEmail());
                });
            });
        }
    }

    private void updatePassword() {
        if (password.getText().toString().equals(Newpassword.getText().toString())) {
            UserInterface.findOne(email).thenAccept(res -> {
                res.setName(name.getText().toString()).setAvatar(image.toString()).update(Newpassword.getText().toString()).thenAccept(v -> {
                    Log.d("user-update", "user updated " + res.getEmail());
                });
            });
        }
    }

    private void updateName() {
        if (!name.getText().toString().isEmpty()) {
            UserInterface.findOne(email).thenAccept(res -> {
                res.setName(name.getText().toString()).update().thenAccept(v -> {
                    Log.d("user-update", "user updated " + res.getEmail());
                });
            });
        }
    }
}
