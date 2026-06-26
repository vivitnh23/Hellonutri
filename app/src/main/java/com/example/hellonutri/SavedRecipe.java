package com.example.hellonutri;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "saved_recipes")
public class SavedRecipe {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String title;
    public String kcal;
    public String bahan;
    public String cara;
    public String imagePath; // Untuk menyimpan path foto jika ada
    public String tanggalSimpan;

    public SavedRecipe(String title, String kcal, String bahan, String cara, String imagePath, String tanggalSimpan) {
        this.title = title;
        this.kcal = kcal;
        this.bahan = bahan;
        this.cara = cara;
        this.imagePath = imagePath;
        this.tanggalSimpan = tanggalSimpan;
    }
}