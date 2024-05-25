package com.example.floraguard_pfa_2024.User;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.floraguard_pfa_2024.User.Exceptions.RegisterException;
import com.example.floraguard_pfa_2024.User.Exceptions.RegisterExceptionCause;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface UserInterface {
    public CompletableFuture<Void> delete();
    public CompletableFuture<Void> update(String newPassword);
    static void logout() {
        UserModel.mAuth.signOut();
    };

    static CompletableFuture<UserModel> auth(boolean needAdmin) {
        CompletableFuture<UserModel> future = new CompletableFuture<>();
        FirebaseUser currentUser = UserModel.mAuth.getCurrentUser();

        if(currentUser == null) {
            future.complete(null);

            return future;
        };

        String email = currentUser.getEmail();

        UserInterface.findOne(email).thenApply(res -> {
            if(res == null) future.complete(null);
            else if (needAdmin && !(res instanceof Admin)) future.complete(null);
            else future.complete(res);

            return null;
        });

        return future;
    }

    static CompletableFuture<UserModel> auth() {
        return auth(false);
    }

    static CompletableFuture<UserModel> findOne(String email) {
        CompletableFuture<UserModel> future = new CompletableFuture<>();

        UserModel.db.collection("users").document(email).get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> data = document.getData();
                            int role = (int) ((long) data.get("role"));
                            String name = (String) data.get("name");
                            String email = (String) data.get("email");
                            String avatar = (String) data.get("avatar");

                            if(role == 0) future.complete(new Admin(name, email, avatar));
                            else future.complete(new UserModel(name, email, avatar));
                        } else {
                            future.complete(null);
                        }
                    } else {
                        Log.d("user", "get failed with ", task.getException());
                    }
                }
            });

        return future;
    }

    static CompletableFuture<UserModel> login(String email, String password) {
        CompletableFuture<UserModel> future = new CompletableFuture<>();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    UserInterface.auth().thenApply(res -> {
                        future.complete(res);

                        return null;
                    });
                }
                else future.complete(null);
            });

        return future;
    }

    static CompletableFuture<UserModel> create(String name, String email, String password, String avatar) {
        CompletableFuture<UserModel> future = new CompletableFuture<>();
        Map<String, Object> userData = new HashMap<>();

        userData.put("name", name);
        userData.put("email", email);
        userData.put("avatar", avatar);
        userData.put("role", 1);

        UserModel.mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Registration success
                        UserModel.db.collection("users").document(email)
                            .set(userData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    future.complete(new UserModel(name, email, avatar));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("user", "Error writing user document", e);
                                }
                            });
                    } else {
                        // If registration fails
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            UserInterface.findOne(email).thenAccept(res -> {
                                if(res == null) {
                                    UserModel.db.collection("users").document(email)
                                        .set(userData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                future.complete(new UserModel(name, email, avatar));
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("user", "Error writing user document", e);
                                            }
                                        });
                                } else {
                                    future.completeExceptionally(new RegisterException(
                                            RegisterExceptionCause.DUBLICATED_EMAIL,
                                            "this email is allready exist."
                                    ));
                                }
                            });
                        };
                    }
                }
            });

        return future;
    }

    static CompletableFuture<LinkedList<UserModel>> all() {
        CompletableFuture<LinkedList<UserModel>> future = new CompletableFuture<>();
        LinkedList<UserModel> list = new LinkedList<>();

        UserModel.db.collection("users")
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        UserModel user = new UserModel(
                            document.getString("name"),
                            document.getString("email"),
                            document.getString("avatar")
                        );

                        list.add(user);
                    }

                    future.complete(list);
                }
            });

        return  future;
    }

    static CompletableFuture<Long> count() {
        CompletableFuture<Long> future = new CompletableFuture<>();

        UserModel.db.collection("users").count().get(AggregateSource.SERVER)
            .addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        future.complete(task.getResult().getCount());
                    }
                }
            });

        return future;
    }
}
