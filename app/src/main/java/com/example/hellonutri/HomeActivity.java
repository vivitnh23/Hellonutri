package com.example.hellonutri;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler; // Tambahan untuk Auto-Slide
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private TextView tvGreeting, tvAngkaBMR, tvTargetMakan;
    private ViewPager2 viewPagerSlider;
    private BottomNavigationView bottomNavigation;

    // Variabel untuk Auto-Slide
    private Handler sliderHandler = new Handler();
    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            // Cek jika adapter tidak null
            if (viewPagerSlider.getAdapter() != null) {
                int nextItem = viewPagerSlider.getCurrentItem() + 1;
                int totalItems = viewPagerSlider.getAdapter().getItemCount();

                // Jika sudah sampai akhir, kembali ke awal
                if (nextItem >= totalItems) {
                    viewPagerSlider.setCurrentItem(0, true);
                } else {
                    viewPagerSlider.setCurrentItem(nextItem, true);
                }

                // Jadwalkan lagi dalam 3 detik (3000 ms)
                sliderHandler.postDelayed(this, 3000);
            }
        }
    };

    // Launcher untuk kamera (Scan Makanan)
    private final ActivityResultLauncher<Intent> scanCameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bitmap foto = (Bitmap) result.getData().getExtras().get("data");
                    if (foto != null) {
                        Intent intent = new Intent(HomeActivity.this, ResultScanActivity.class);
                        intent.putExtra("FOTO_SCAN", foto);
                        startActivity(intent);
                    }
                }
            }
    );

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    bukaKamera();
                } else {
                    Toast.makeText(this, "Izin kamera diperlukan", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 1. Inisialisasi View
        tvGreeting = findViewById(R.id.tvGreeting);
        tvAngkaBMR = findViewById(R.id.tvAngkaBMR);
        tvTargetMakan = findViewById(R.id.tvTargetMakan);
        viewPagerSlider = findViewById(R.id.viewPagerSlider);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        ImageView ivHistory = findViewById(R.id.ivHistory);

        CardView btnScanMakanan = findViewById(R.id.btnScanMakanan);
        CardView btnTanyaAI = findViewById(R.id.btnTanyaAI);
        CardView btnResepSehat = findViewById(R.id.btnResepSehat);

        // 2. Setup Fungsi UI
        setupNavColorAndLogic();
        setupSlider();
        updateHomeUI();

        // 3. Logika Klik Tombol
        if (btnScanMakanan != null) {
            btnScanMakanan.setOnClickListener(v -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    bukaKamera();
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                }
            });
        }

        if (btnTanyaAI != null) {
            btnTanyaAI.setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        }

        if (btnResepSehat != null) {
            btnResepSehat.setOnClickListener(v -> startActivity(new Intent(this, RecipeActivity.class)));
        }

        if (ivHistory != null) {
            ivHistory.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
        }
    }

    private void bukaKamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            scanCameraLauncher.launch(takePictureIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Kamera tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupNavColorAndLogic() {
        int[][] states = new int[][]{ new int[]{android.R.attr.state_checked}, new int[]{-android.R.attr.state_checked} };
        int[] colors = new int[]{ Color.parseColor("#4CAF50"), Color.parseColor("#888888") };
        ColorStateList colorList = new ColorStateList(states, colors);

        bottomNavigation.setItemIconTintList(colorList);
        bottomNavigation.setItemTextColor(colorList);
        bottomNavigation.setSelectedItemId(R.id.nav_home);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_chat) {
                startActivity(new Intent(this, ChatActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_saved) {
                startActivity(new Intent(this, SavedActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return true;
        });
    }

    private void setupSlider() {
        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.slide1);
        images.add(R.drawable.slide2);
        images.add(R.drawable.slide3);

        SliderAdapter adapter = new SliderAdapter(images);
        viewPagerSlider.setAdapter(adapter);

        viewPagerSlider.setOffscreenPageLimit(3);
        if (viewPagerSlider.getChildAt(0) != null) {
            viewPagerSlider.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
        }

        // Jalankan Auto-Slide pertama kali
        sliderHandler.postDelayed(sliderRunnable, 3000);

        // Berhenti Auto-Slide jika user menyentuh/menggeser manual
        viewPagerSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });
    }

    private void updateHomeUI() {
        SharedPreferences pref = getSharedPreferences("HelloNutriPrefs", MODE_PRIVATE);
        String nama = pref.getString("name", "User");
        int umur = pref.getInt("age", 20);
        int tinggi = pref.getInt("height", 160);
        int berat = pref.getInt("weight", 60);
        String gender = pref.getString("gender", "Perempuan");
        String tujuan = pref.getString("goal", "Hidup Sehat");

        double bmr = (gender.equalsIgnoreCase("Laki-laki")) ?
                (10 * berat) + (6.25 * tinggi) - (5 * umur) + 5 :
                (10 * berat) + (6.25 * tinggi) - (5 * umur) - 161;

        tvGreeting.setText("Hai, " + nama + "! 👋");
        tvAngkaBMR.setText((int) bmr + " kkal/hari");

        int targetMakan = tujuan.equals("Menurunkan Berat Badan") ? (int)bmr - 300 :
                tujuan.equals("Membentuk Otot") ? (int)bmr + 400 : (int)bmr + 100;

        tvTargetMakan.setText(targetMakan + " kkal/hari");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Berhenti saat aplikasi di background agar hemat baterai
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHomeUI();
        bottomNavigation.setSelectedItemId(R.id.nav_home);
        // Jalankan lagi saat aplikasi dibuka kembali
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }
}