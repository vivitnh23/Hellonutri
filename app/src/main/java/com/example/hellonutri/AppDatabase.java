package com.example.hellonutri;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// Database mengelola tabel: NutritionLog, ChatMessage, SavedRecipe, dan SavedScan
@Database(entities = {NutritionLog.class, ChatMessage.class, SavedRecipe.class, SavedScan.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    public abstract NutritionDao nutritionDao();
    public abstract ChatDao chatDao();
    public abstract SavedDao savedDao();

    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "hellonutri_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries() // Mempermudah akses data tanpa asynctask tambahan
                    .build();
        }
        return instance;
    }
}