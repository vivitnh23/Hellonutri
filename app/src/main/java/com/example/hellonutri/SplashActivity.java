package com.example.hellonutri;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class SplashActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final long SPLASH_DELAY_MS = 5000; // 5 detik
    private static final long LOCATION_TIMEOUT_MS = 4000; // 4 detik

    private FusedLocationProviderClient fusedLocationClient;
    private TextView locationText;
    private TextView welcomeSplashText;
    private ImageView flagImage;
    private Handler handler;

    private final AtomicBoolean isTransitioned = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        locationText = findViewById(R.id.locationText);
        welcomeSplashText = findViewById(R.id.welcomeSplashText);
        flagImage = findViewById(R.id.flagImage);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        handler = new Handler(Looper.getMainLooper());

        checkLocationPermission();

        // Atur teks default saat inisialisasi, ini akan diganti setelah lokasi terdeteksi
        locationText.setText("Mendeteksi Lokasi...");
    }

    // ================== LOGIKA LOKALE/BAHASA (Tidak Berubah) ==================

    private void setAppLocale(String localeCode) {
        if (localeCode != null) {
            if (!Locale.getDefault().getLanguage().equalsIgnoreCase(localeCode)) {
                Locale newLocale = new Locale(localeCode);
                Locale.setDefault(newLocale);

                android.content.res.Configuration config = new android.content.res.Configuration();
                config.setLocale(newLocale);

                getBaseContext().getResources().updateConfiguration(
                        config,
                        getBaseContext().getResources().getDisplayMetrics()
                );
            }
        }
    }

    private String getLocaleCode(String countryName) {
        if (countryName.equalsIgnoreCase("Indonesia")) {
            flagImage.setImageResource(R.drawable.flag_indonesia);
            return "in";
        } else if (countryName.equalsIgnoreCase("Malaysia")) {
            flagImage.setImageResource(R.drawable.flag_malaysia);
            return "ms";
        } else if (countryName.equalsIgnoreCase("Japan")) {
            flagImage.setImageResource(R.drawable.flag_japan);
            return "ja";
        } else if (countryName.equalsIgnoreCase("United States") || countryName.equalsIgnoreCase("USA")) {
            flagImage.setImageResource(R.drawable.flag_usa);
            return "en";
        } else {
            flagImage.setImageResource(R.drawable.flag_uknown);
            return null;
        }
    }

    /**
     * Fungsi baru: Memperbarui teks Selamat Datang dan Negara
     * setelah Locale diatur.
     */
    private void updateLocalizedTextViews(String countryName) {
        // Tampilkan ucapan Selamat Datang sesuai bahasa
        welcomeSplashText.setText(getString(R.string.welcome_splash_text));

        // Tampilkan nama negara sesuai bahasa
        locationText.setText(getString(R.string.location_label) + countryName);
    }

    // ================== LOGIKA LOKASI & TRANSISI (DIUBAH) ==================

    /**
     * FUNGSI TRANSISI DIREVISI: Mengubah target Intent ke LoadingActivity.
     * Nama fungsi diubah dari delayedTransitionToMain() menjadi delayedTransitionToLoading().
     */
    private synchronized void delayedTransitionToLoading() {
        if (isTransitioned.compareAndSet(false, true)) {
            handler.postDelayed(() -> {
                // Pindah dari Splash ke Onboarding
                Intent intent = new Intent(SplashActivity.this, OnboardingActivity.class);
                startActivity(intent);
                finish();
            }, SPLASH_DELAY_MS);
        }
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getUserLocation();
        }
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            locationText.setText(getString(R.string.permission_denied));
            welcomeSplashText.setText(getString(R.string.welcome_splash_text)); // Fallback
            delayedTransitionToLoading(); // Panggilan fungsi diubah
            return;
        }

        // Safety Net: Timeout
        handler.postDelayed(() -> {
            if (!isTransitioned.get()) {
                locationText.setText(getString(R.string.location_timeout));
                welcomeSplashText.setText(getString(R.string.welcome_splash_text)); // Fallback
                delayedTransitionToLoading(); // Panggilan fungsi diubah
            }
        }, LOCATION_TIMEOUT_MS);

        Task<Location> locationTask = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY, null
        );

        locationTask.addOnSuccessListener(this, location -> {
            if (!isTransitioned.get()) {
                if (location != null) {
                    getCountryFromLocation(location);
                } else {
                    locationText.setText(getString(R.string.location_unavailable));
                    welcomeSplashText.setText(getString(R.string.welcome_splash_text)); // Fallback
                }
                delayedTransitionToLoading(); // Panggilan fungsi diubah
            }
        }).addOnFailureListener(e -> {
            if (!isTransitioned.get()) {
                locationText.setText("Gagal: " + e.getMessage());
                welcomeSplashText.setText(getString(R.string.welcome_splash_text)); // Fallback
                delayedTransitionToLoading(); // Panggilan fungsi diubah
            }
        });
    }

    private void getCountryFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses =
                    geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                String countryName = addresses.get(0).getCountryName();

                // 1. Dapatkan kode Locale dan set bendera
                String localeCode = getLocaleCode(countryName);

                // 2. Terapkan perubahan Locale
                setAppLocale(localeCode);

                // 3. PENTING: Perbarui teks menggunakan fungsi baru (agar teksnya sudah ganti bahasa)
                updateLocalizedTextViews(countryName);

            } else {
                locationText.setText(getString(R.string.geocoder_failed));
            }
        } catch (IOException e) {
            locationText.setText("Geocoder Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ================== HASIL PERMINTAAN IZIN ==================

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Izin disetujui.", Toast.LENGTH_SHORT).show();
                getUserLocation();
            } else {
                locationText.setText(getString(R.string.permission_denied));
                welcomeSplashText.setText(getString(R.string.welcome_splash_text)); // Fallback
                Toast.makeText(this, "Izin ditolak. Melanjutkan...", Toast.LENGTH_LONG).show();
                delayedTransitionToLoading(); // Panggilan fungsi diubah
            }
        }
    }
}