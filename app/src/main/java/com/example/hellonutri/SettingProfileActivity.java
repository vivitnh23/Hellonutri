package com.example.hellonutri;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SettingProfileActivity extends AppCompatActivity {

    private EditText etFullName, etAge, etWeight, etHeight;
    private Spinner spGender, spTujuanUtama;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_profile);

        // 1. Inisialisasi View berdasarkan ID di XML
        etFullName = findViewById(R.id.etFullName);
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        spGender = findViewById(R.id.spGender);
        spTujuanUtama = findViewById(R.id.spTujuanUtama);
        btnSave = findViewById(R.id.btnSave);

        // 2. Setup Spinner Gender dengan Hint
        setupSpinnerGender();

        // 3. Setup Spinner Tujuan Utama dengan Hint
        setupSpinnerTujuan();

        // 4. Logika Tombol Simpan
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSave();
            }
        });
    }

    private void setupSpinnerGender() {
        String[] genders = {"Contoh : perempuan", "Laki-laki", "Perempuan"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, genders) {
            @Override
            public boolean isEnabled(int position) {
                // Nonaktifkan item pertama (indeks 0) agar tidak bisa dipilih
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY); // Warna abu-abu untuk hint
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spGender.setAdapter(adapter);
    }

    private void setupSpinnerTujuan() {
        String[] goals = {"Pilih tujuanmu", "Hidup Sehat", "Menurunkan Berat Badan", "Membentuk Otot"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, goals) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spTujuanUtama.setAdapter(adapter);
    }

    private void validateAndSave() {
        String name = etFullName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();

        int genderPos = spGender.getSelectedItemPosition();
        int goalPos = spTujuanUtama.getSelectedItemPosition();

        // Validasi jika ada field yang kosong
        if (name.isEmpty() || ageStr.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty()) {
            Toast.makeText(this, "Harap lengkapi semua data!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validasi jika spinner belum dipilih (masih di posisi hint)
        if (genderPos == 0 || goalPos == 0) {
            Toast.makeText(this, "Harap pilih Gender dan Tujuan Utama!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ambil nilai final
        String gender = spGender.getSelectedItem().toString();
        String goal = spTujuanUtama.getSelectedItem().toString();
        int age = Integer.parseInt(ageStr);
        int weight = Integer.parseInt(weightStr);
        int height = Integer.parseInt(heightStr);

        // Simpan ke SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("HelloNutriPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("name", name);
        editor.putInt("age", age);
        editor.putInt("weight", weight);
        editor.putInt("height", height);
        editor.putString("gender", gender);
        editor.putString("goal", goal);
        editor.putBoolean("isProfileSet", true); // Penanda bahwa user sudah isi profil
        editor.apply();

        // Tampilkan pesan sukses dan pindah ke Home
        Toast.makeText(this, "Profil berhasil disimpan!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SettingProfileActivity.this, HomeActivity.class);
        startActivity(intent);
        finish(); // Menutup activity ini agar tidak bisa kembali dengan tombol back
    }
}