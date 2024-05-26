package com.example.floraguard_pfa_2024.User;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.concurrent.CompletableFuture;

public class UserModel implements UserInterface {
    enum UserRole {
        USER, ADMIN
    }

    private String name;
    private String email;
    private String avatar;
    private final int role = 1;

    protected static final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    protected static final FirebaseFirestore db = FirebaseFirestore.getInstance();;
    protected static final FirebaseStorage storage = FirebaseStorage.getInstance();

    public UserModel(String name, String email, String avatar) {
        this.name = name;
        this.email = email;
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public UserModel setName(String name) {
         this.name = name;

         return this;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatar() {
        return avatar;
    }

    public UserModel setAvatar(String avatar) {
        this.avatar = avatar;

        return this;
    }

    public UserRole role() {

        return UserRole.USER;
    }

    @Override
    public CompletableFuture<Void> delete() {
        CompletableFuture<Void> future = new CompletableFuture();

        db.collection("users").document(email)
            .delete()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    future.complete(null);
                }
            });

        return future;
    }

    @Override
    public CompletableFuture<Void> update(String newPassword) {
        CompletableFuture<Void> future = new CompletableFuture();
        DocumentReference taskRef = db.collection("users").document(email);

        UserInterface.auth().thenAccept(authUser -> {
            if(authUser.getEmail().equals(email)) {
                taskRef.update(
                        "name", name,
                        "avatar", avatar
                ).addOnSuccessListener(documentReference -> {
                    FirebaseUser user = mAuth.getCurrentUser();

                    if(newPassword != null) {
                        user.updatePassword(newPassword);
                    }

                    future.complete(null);
                });
            }
        });

        return future;
    }

    public CompletableFuture<Void> update() {
        return update(null);
    }
}
