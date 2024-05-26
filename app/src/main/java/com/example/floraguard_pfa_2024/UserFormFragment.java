package com.example.floraguard_pfa_2024;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.floraguard_pfa_2024.User.UserInterface;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFormFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText name;
    private EditText email;
    private EditText password;
    private Button button;


    public UserFormFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFormFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFormFragment newInstance(String param1, String param2) {
        UserFormFragment fragment = new UserFormFragment();
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
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_user_form, container, false);
        name=rootView.findViewById(R.id.ed_name);
        email=rootView.findViewById(R.id.ed_email);
        password=rootView.findViewById(R.id.ed_password);
        button=rootView.findViewById(R.id.btn_save);
        button.setOnClickListener(v->{CreateUser();});
        return rootView;

    }

    private void CreateUser() {
        String nameValue=name.getText().toString();
        String EmailValue=email.getText().toString();
        String PasswordValue=password.getText().toString();
        UserInterface.create(nameValue,EmailValue,PasswordValue,"").whenComplete((res, exception) -> {
            if(exception == null) {
                Log.d("user-create", res.getName());
                showToast("user created");
                goToMainActivity();
            } else { // if email is duplicated
                Log.d("user", exception.getMessage());
            }
        });


    }
    private void goToMainActivity() {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        startActivity(intent);
        // If you want to remove this fragment from the back stack:
        requireActivity().getSupportFragmentManager().popBackStack();
    }
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

}