package com.example.floraguard_pfa_2024.Plant;

public enum PlantType {
    // Define enum constants with associated string values
    TOMATO("TOMATO");


    // Private field to hold the string value
    private final String description;

    // Private constructor to initialize the string value
    PlantType(String description) {
        this.description = description;
    }


    // Public method to retrieve the associated string value
    public String getDescription() {
        return description;
    }

    // Static method to get the enum constant by description
    public static PlantType fromDescription(String description) {
        for (PlantType status : PlantType.values()) {
            if (status.getDescription().equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No PlantType enum constant with description " + description);
    }
}
