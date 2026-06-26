package com.example.hellonutri;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "nutrition_logs")
public class NutritionLog {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String foodName;
    public String calories;
    public String date;

    public NutritionLog(String foodName, String calories, String date) {
        this.foodName = foodName;
        this.calories = calories;
        this.date = date;
    }
}