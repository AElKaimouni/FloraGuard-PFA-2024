package com.example.floraguard_pfa_2024.Plant;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class PlantModel {
    private String id;
    private String name;
    private String image;
    private PlantType type;

    public PlantModel(String id, String name, String image, String type) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.type = PlantType.fromDescription(type);
    }

    protected static final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    protected static final FirebaseFirestore db = FirebaseFirestore.getInstance();;
    protected static final FirebaseStorage storage = FirebaseStorage.getInstance();

    public String getName() {
        return this.name;
    }

    public String getID() {
        return this.id;
    }

    public String getImage() {
        return this.image;
    }

    public PlantType getType() {
        return this.type;
    }

}
