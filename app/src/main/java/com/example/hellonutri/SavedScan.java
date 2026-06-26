package com.example.hellonutri;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "saved_scans")
public class SavedScan {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String foodName;
    public String calories;
    public String protein;
    public String carbs;
    public String fat;
    public String imagePath;
    public String komposisi;
    public String tanggalSimpan;

    public SavedScan(String foodName, String calories, String protein, String carbs, String fat, String imagePath, String komposisi, String tanggalSimpan) {
        this.foodName = foodName;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.imagePath = imagePath;
        this.komposisi = komposisi;
        this.tanggalSimpan = tanggalSimpan;
    }
}