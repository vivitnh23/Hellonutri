package com.example.hellonutri;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private AppDatabase db;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        db = AppDatabase.getInstance(this);
        rvHistory = findViewById(R.id.rvHistory);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        
        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        // Ambil riwayat unik (per sesi)
        List<ChatMessage> sessionList = db.chatDao().getUniqueSessions();
        HistoryAdapter adapter = new HistoryAdapter(sessionList);
        rvHistory.setAdapter(adapter);

        setupBottomNavigation();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnStartChat).setOnClickListener(v -> {
            startActivity(new Intent(this, ChatActivity.class));
        });
    }

    private void setupBottomNavigation() {
        int[][] states = new int[][]{ new int[]{android.R.attr.state_checked}, new int[]{-android.R.attr.state_checked} };
        int[] colors = new int[]{ Color.parseColor("#4CAF50"), Color.parseColor("#888888") };
        ColorStateList colorList = new ColorStateList(states, colors);

        bottomNavigation.setItemIconTintList(colorList);
        bottomNavigation.setItemTextColor(colorList);
        
        // Karena Riwayat adalah bagian dari Chat, kita tandai Nav Chat yang aktif
        bottomNavigation.setSelectedItemId(R.id.nav_chat);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_saved) {
                startActivity(new Intent(this, SavedActivity.class));
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
}