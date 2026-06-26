package com.example.hellonutri;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface NutritionDao {
    @Insert
    void insert(NutritionLog log);

    @Query("SELECT * FROM nutrition_logs ORDER BY id DESC")
    List<NutritionLog> getAllLogs();
}