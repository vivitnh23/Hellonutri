package com.example.hellonutri;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface SavedDao {
    @Insert
    void insertRecipe(SavedRecipe recipe);

    @Query("SELECT * FROM saved_recipes ORDER BY id DESC")
    List<SavedRecipe> getAllSavedRecipes();

    @Query("DELETE FROM saved_recipes WHERE id = :id")
    void deleteRecipeById(int id);

    @Query("DELETE FROM saved_recipes")
    void deleteAllRecipes();

    @Insert
    void insertScan(SavedScan scan);

    @Query("SELECT * FROM saved_scans ORDER BY id DESC")
    List<SavedScan> getAllSavedScans();

    @Query("DELETE FROM saved_scans WHERE id = :id")
    void deleteScanById(int id);

    @Query("DELETE FROM saved_scans")
    void deleteAllScans();
}