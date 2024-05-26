package com.example.floraguard_pfa_2024.Plant;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.floraguard_pfa_2024.User.Admin;
import com.example.floraguard_pfa_2024.User.UserModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface PlantInterface {

    static CompletableFuture<Uri> uploadImage(String path) {
        CompletableFuture<Uri> future = new CompletableFuture<>();

        StorageReference storageRef = PlantModel.storage.getReference();
        Uri file = Uri.fromFile(new File(path));
        StorageReference ref = storageRef.child("plants/" + file.getLastPathSegment());
        UploadTask uploadTask = ref.putFile(file);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    future.complete(downloadUri);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });

        return future;
    }
    static CompletableFuture<PlantModel> create(String name, String image, PlantType type) {
        CompletableFuture<PlantModel> future = new CompletableFuture<>();
        Map<String, Object> userData = new HashMap<>();

        userData.put("name", name);
        userData.put("image", image);
        userData.put("type", type.getDescription());

        PlantModel.db.collection("plants")
            .add(userData)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    future.complete(new PlantModel(documentReference.getId(), name, image, type.getDescription()));
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("Plant", "Error adding document", e);
                }
            });

        return future;
    }
    static CompletableFuture<PlantModel> findOne(String id) {
        CompletableFuture<PlantModel> future = new CompletableFuture<>();

        PlantModel.db.collection("plants").document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> data = document.getData();

                                String id = document.getId();
                                String name = (String) data.get("name");
                                String image = (String) data.get("image");
                                String type = (String) data.get("type");

                                future.complete(new PlantModel(id, name, image, type));
                            } else {
                                future.complete(null);
                            }
                        } else {
                            Log.d("plant", "get failed with ", task.getException());
                        }
                    }
                });

        return future;
    }

    public static CompletableFuture<ArrayList<PlantModel>> table() {
        CompletableFuture<ArrayList<PlantModel>> future = new CompletableFuture<>();
        ArrayList<PlantModel> list = new ArrayList<>();

        PlantModel.db.collection("plants")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            PlantModel plant = new PlantModel(
                                    document.getId(),
                                    document.getString("name"),
                                    document.getString("image"),
                                    document.getString("type")
                            );
                            list.add(plant);
                        }
                        future.complete(list);
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });

        return future;
    }
}
