package com.example.hellonutri;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class ScanDetailActivity extends AppCompatActivity {

    private ImageView ivFood;
    private TextView tvFoodName, tvCalories, tvProtein, tvCarbs, tvFat, tvKomposisi, tvDate;
    private AppDatabase db;
    private int scanId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_detail);

        db = AppDatabase.getInstance(this);

        ivFood = findViewById(R.id.ivFood);
        tvFoodName = findViewById(R.id.tvFoodName);
        tvCalories = findViewById(R.id.tvCalories);
        tvProtein = findViewById(R.id.tvProtein);
        tvCarbs = findViewById(R.id.tvCarbs);
        tvFat = findViewById(R.id.tvFat);
        tvKomposisi = findViewById(R.id.tvKomposisi);
        tvDate = findViewById(R.id.tvDate);

        scanId = getIntent().getIntExtra("ID", -1);
        String name = getIntent().getStringExtra("NAME");
        String calories = getIntent().getStringExtra("CALORIES");
        String protein = getIntent().getStringExtra("PROTEIN");
        String carbs = getIntent().getStringExtra("CARBS");
        String fat = getIntent().getStringExtra("FAT");
        String komposisi = getIntent().getStringExtra("KOMPOSISI");
        String date = getIntent().getStringExtra("DATE");
        String imagePath = getIntent().getStringExtra("IMAGE_PATH");

        tvFoodName.setText(name);
        tvCalories.setText(calories);
        tvProtein.setText(protein);
        tvCarbs.setText(carbs);
        tvFat.setText(fat);
        tvKomposisi.setText(komposisi != null && !komposisi.isEmpty() ? komposisi : "Tidak ada informasi komposisi");
        tvDate.setText(date);

        Glide.with(this)
                .load(imagePath)
                .placeholder(R.drawable.food_default)
                .into(ivFood);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnHapus).setOnClickListener(v -> {
            if (scanId != -1) {
                db.savedDao().deleteScanById(scanId);
                Toast.makeText(this, "Berhasil dihapus", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}