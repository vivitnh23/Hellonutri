package com.example.hellonutri;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Menghubungkan Java dengan file layout XML yang kamu buat
        setContentView(R.layout.activity_onboarding);

        // Inisialisasi tombol "Lanjutkan" berdasarkan ID di XML
        AppCompatButton btnContinue = findViewById(R.id.btnContinue);

        // Logika ketika tombol diklik
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Berpindah dari Onboarding ke MainActivity
                Intent intent = new Intent(OnboardingActivity.this, SettingProfileActivity.class);
                startActivity(intent);

                // Menutup OnboardingActivity agar user tidak bisa kembali ke sini dengan tombol back
                finish();
            }
        });
    }
}