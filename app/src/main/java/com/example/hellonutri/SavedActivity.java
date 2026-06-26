package com.example.hellonutri;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class SavedActivity extends AppCompatActivity {

    private RecyclerView rvSaved;
    private TextView btnTabRecipe, btnTabScan, tvSectionTitle;
    private AppDatabase db;
    private BottomNavigationView bottomNavigation;
    private boolean isRecipeTab = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        db = AppDatabase.getInstance(this);

        rvSaved = findViewById(R.id.rvSaved);
        btnTabRecipe = findViewById(R.id.btnTabRecipe);
        btnTabScan = findViewById(R.id.btnTabScan);
        tvSectionTitle = findViewById(R.id.tvSectionTitle);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        rvSaved.setLayoutManager(new LinearLayoutManager(this));

        setupBottomNavigation();
        loadData();

        btnTabRecipe.setOnClickListener(v -> {
            isRecipeTab = true;
            updateTabUI();
            loadData();
        });

        btnTabScan.setOnClickListener(v -> {
            isRecipeTab = false;
            updateTabUI();
            loadData();
        });

        // Fitur Hapus Semua (Ikon Tong Sampah)
        findViewById(R.id.btnDeleteAll).setOnClickListener(v -> {
            String target = isRecipeTab ? "semua resep" : "semua hasil scan";
            new AlertDialog.Builder(this)
                    .setTitle("Hapus Semua")
                    .setMessage("Apakah Anda yakin ingin menghapus " + target + " yang tersimpan?")
                    .setPositiveButton("Hapus", (dialog, which) -> {
                        if (isRecipeTab) {
                            db.savedDao().deleteAllRecipes();
                        } else {
                            db.savedDao().deleteAllScans();
                        }
                        loadData();
                        Toast.makeText(this, "Berhasil dihapus", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }

    private void loadData() {
        if (isRecipeTab) {
            tvSectionTitle.setText("Resep Sehat Tersimpan");
            List<SavedRecipe> recipes = db.savedDao().getAllSavedRecipes();
            SavedRecipeAdapter adapter = new SavedRecipeAdapter(recipes);
            rvSaved.setAdapter(adapter);
        } else {
            tvSectionTitle.setText("Hasil Scan Tersimpan");
            List<SavedScan> scans = db.savedDao().getAllSavedScans();
            SavedScanAdapter adapter = new SavedScanAdapter(scans);
            rvSaved.setAdapter(adapter);
        }
    }

    private void updateTabUI() {
        if (isRecipeTab) {
            btnTabRecipe.setBackgroundResource(R.drawable.rounded_button_green);
            btnTabRecipe.setTextColor(Color.WHITE);
            btnTabScan.setBackground(null);
            btnTabScan.setTextColor(Color.parseColor("#888888"));
        } else {
            btnTabScan.setBackgroundResource(R.drawable.rounded_button_green);
            btnTabScan.setTextColor(Color.WHITE);
            btnTabRecipe.setBackground(null);
            btnTabRecipe.setTextColor(Color.parseColor("#888888"));
        }
    }

    private void setupBottomNavigation() {
        int[][] states = new int[][]{ new int[]{android.R.attr.state_checked}, new int[]{-android.R.attr.state_checked} };
        int[] colors = new int[]{ Color.parseColor("#4CAF50"), Color.parseColor("#888888") };
        ColorStateList colorList = new ColorStateList(states, colors);

        bottomNavigation.setItemIconTintList(colorList);
        bottomNavigation.setItemTextColor(colorList);
        bottomNavigation.setSelectedItemId(R.id.nav_saved);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, ChatActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}