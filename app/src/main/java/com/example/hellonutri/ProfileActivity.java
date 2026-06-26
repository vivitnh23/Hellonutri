package com.example.hellonutri;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProfileName, tvProfileGoal;
    private ImageView imgProfileAvatar;
    private SharedPreferences pref;
    private BottomNavigationView bottomNavigation;

    // Launcher untuk mengambil foto dari Gallery
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        imgProfileAvatar.setImageBitmap(selectedImage);
                        saveImageToPref(selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        pref = getSharedPreferences("HelloNutriPrefs", MODE_PRIVATE);

        // Inisialisasi View
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileGoal = findViewById(R.id.tvProfileGoal);
        imgProfileAvatar = findViewById(R.id.imgProfileAvatar);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        LinearLayout menuDataDiri = findViewById(R.id.menuDataDiri);
        LinearLayout menuTujuan = findViewById(R.id.menuTujuan);
        LinearLayout menuTentang = findViewById(R.id.menuTentang);

        setupNavColorAndLogic();
        loadData();

        // 1. KLIK FOTO (Ganti Foto Profil)
        imgProfileAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        // 2. KLIK UBAH PROFIL
        findViewById(R.id.btnEditProfilSimple).setOnClickListener(v -> showEditDialog());

        // 3. KLIK INFORMASI PRIBADI (Detail Data Diri)
        menuDataDiri.setOnClickListener(v -> {
            int umur = pref.getInt("age", 20);
            int tinggi = pref.getInt("height", 160);
            int berat = pref.getInt("weight", 60);
            String gender = pref.getString("gender", "Perempuan");
            
            // Hitung BMR untuk ditampilkan di info detail
            double bmr = (gender.equalsIgnoreCase("Laki-laki")) ?
                    (10 * berat) + (6.25 * tinggi) - (5 * umur) + 5 :
                    (10 * berat) + (6.25 * tinggi) - (5 * umur) - 161;

            String detail = "Jenis Kelamin: " + gender + "\n" +
                    "Umur: " + umur + " tahun\n" +
                    "Tinggi: " + tinggi + " cm\n" +
                    "Berat: " + berat + " kg\n\n" +
                    "Kebutuhan Kalori (BMR):\n" + (int)bmr + " kkal/hari";
            
            showInfoDialog("Informasi Pribadi", detail);
        });

        // 4. KLIK TARGET KESEHATAN
        menuTujuan.setOnClickListener(v -> {
            String[] goals = {"Hidup Sehat", "Menurunkan Berat Badan", "Membentuk Otot"};
            new AlertDialog.Builder(this)
                    .setTitle("Pilih Target Baru")
                    .setItems(goals, (dialog, which) -> {
                        pref.edit().putString("goal", goals[which]).apply();
                        loadData();
                        Toast.makeText(this, "Target berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    }).show();
        });

        // 5. KLIK TENTANG APLIKASI
        menuTentang.setOnClickListener(v -> 
            showInfoDialog("Tentang HelloNutri", "Versi 1.2.0\n\nAplikasi asisten nutrisi cerdas berbasis AI untuk membantu Anda mencapai pola hidup yang lebih sehat.")
        );
    }

    private void loadData() {
        tvProfileName.setText(pref.getString("name", "User"));
        tvProfileGoal.setText("Target: " + pref.getString("goal", "Hidup Sehat"));

        String encodedImage = pref.getString("profile_image", "");
        if (!encodedImage.isEmpty()) {
            byte[] b = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            imgProfileAvatar.setImageBitmap(bitmap);
        }
    }

    private void setupNavColorAndLogic() {
        int[][] states = new int[][]{ new int[]{android.R.attr.state_checked}, new int[]{-android.R.attr.state_checked} };
        int[] colors = new int[]{ Color.parseColor("#4CAF50"), Color.parseColor("#888888") };
        ColorStateList colorList = new ColorStateList(states, colors);

        bottomNavigation.setItemIconTintList(colorList);
        bottomNavigation.setItemTextColor(colorList);
        bottomNavigation.setSelectedItemId(R.id.nav_profile);

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
            } else if (id == R.id.nav_saved) {
                startActivity(new Intent(this, SavedActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return true;
        });
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ubah Nama Panggilan");
        final EditText input = new EditText(this);
        input.setPadding(40, 20, 40, 20);
        input.setText(tvProfileName.getText().toString());
        builder.setView(input);

        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                pref.edit().putString("name", newName).apply();
                loadData();
            }
        });
        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    private void showInfoDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Tutup", null)
                .show();
    }

    private void saveImageToPref(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        pref.edit().putString("profile_image", encodedImage).apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigation.setSelectedItemId(R.id.nav_profile);
    }
}