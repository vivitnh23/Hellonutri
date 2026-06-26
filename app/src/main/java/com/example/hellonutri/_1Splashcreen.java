package com.example.hellonutri;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

public class _1Splashcreen extends AppCompatActivity {

    private static final int SPLASH_DURATION = 10000; // 10 detik

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1_splashcreen);

        // 1. Inisialisasi ProgressBar dari XML
        ProgressBar progressBar = findViewById(R.id.viewProgressBarMock);

        // 2. Membuat animasi pengisian progress bar dari nilai 0 ke 100
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 100);
        animation.setDuration(SPLASH_DURATION); // Durasi gerakan disamakan 10 detik
        animation.setInterpolator(new LinearInterpolator()); // Biar jalannya stabil rata
        animation.start(); // Mulai jalankan animasi loading

        // 3. Handler untuk memindahkan halaman setelah 10 detik selesai
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Silakan ganti OnboardingActivity dengan nama activity tujuan kamu selanjutnya
                Intent intent = new Intent(_1Splashcreen.this, SplashActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DURATION);
    }
}