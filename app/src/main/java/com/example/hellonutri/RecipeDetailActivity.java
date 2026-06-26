package com.example.hellonutri;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvKcal, tvBahan, tvCara;
    private ImageView ivFoto;
    private GenerativeModelFutures model;
    private AppDatabase db;
    private String urlGambar;
    private int recipeId;

    // API Key disembunyikan menggunakan BuildConfig
    private final String API_KEY = BuildConfig.GEMINI_API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        db = AppDatabase.getInstance(this);

        tvTitle = findViewById(R.id.tvDetailTitle);
        tvKcal = findViewById(R.id.tvDetailKcal);
        tvBahan = findViewById(R.id.tvBahan);
        tvCara = findViewById(R.id.tvCara);
        ivFoto = findViewById(R.id.ivDetailResep);
        
        Button btnSimpan = findViewById(R.id.btnSimpanResep);
        Button btnHapus = findViewById(R.id.btnHapusResep);

        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash-lite", API_KEY);
        model = GenerativeModelFutures.from(gm);

        recipeId = getIntent().getIntExtra("ID", -1);
        String judul = getIntent().getStringExtra("JUDUL");
        String kkal = getIntent().getStringExtra("KKAL");
        urlGambar = getIntent().getStringExtra("URL_GAMBAR");
        boolean fromSaved = getIntent().getBooleanExtra("FROM_SAVED", false);

        if (judul != null) {
            tvTitle.setText(judul);
            tvKcal.setText(kkal);

            if (urlGambar != null && !urlGambar.isEmpty()) {
                RequestOptions options = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_report_image)
                        .centerCrop();

                Glide.with(this)
                        .load(urlGambar)
                        .apply(options)
                        .into(ivFoto);
            }

            if (fromSaved) {
                String bahan = getIntent().getStringExtra("BAHAN");
                String cara = getIntent().getStringExtra("CARA");
                tvBahan.setText(bahan);
                tvCara.setText(cara);
                
                btnSimpan.setVisibility(View.GONE);
                btnHapus.setVisibility(View.VISIBLE);
            } else {
                btnSimpan.setVisibility(View.VISIBLE);
                btnHapus.setVisibility(View.GONE);
                fetchFullRecipe(judul);
            }
        }

        findViewById(R.id.btnBackDetail).setOnClickListener(v -> finish());
        
        btnSimpan.setOnClickListener(v -> saveRecipe());
        
        btnHapus.setOnClickListener(v -> {
            if (recipeId != -1) {
                db.savedDao().deleteRecipeById(recipeId);
                Toast.makeText(this, "Resep dihapus dari tersimpan", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void saveRecipe() {
        String judul = tvTitle.getText().toString();
        String kkal = tvKcal.getText().toString();
        String bahan = tvBahan.getText().toString();
        String cara = tvCara.getText().toString();

        if (bahan.contains("Sedang meracik") || bahan.contains("Gagal")) {
            Toast.makeText(this, "Tunggu resep selesai dimuat", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());

        SavedRecipe recipe = new SavedRecipe(judul, kkal, bahan, cara, urlGambar, currentDate);
        db.savedDao().insertRecipe(recipe);

        Toast.makeText(this, "Resep berhasil disimpan!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void fetchFullRecipe(String query) {
        tvBahan.setText("⏳ Sedang meracik bahan...");
        tvCara.setText("⏳ Menyiapkan langkah masak...");

        String prompt = "Berikan resep lengkap untuk: " + query + ". " +
                "Berikan jawaban dalam Bahasa Indonesia dengan format WAJIB: " +
                "BAHAN: (tulis daftar bahan, tiap bahan di baris baru) " +
                "CARA: (tulis langkah memasak, tiap langkah di baris baru). " +
                "Jangan berikan teks pembuka atau penutup.";

        Content content = new Content.Builder().addText(prompt).build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                runOnUiThread(() -> {
                    String rawText = result.getText();
                    if (rawText != null && rawText.contains("CARA:")) {
                        String[] parts = rawText.split("CARA:");
                        String bahan = parts[0].replace("BAHAN:", "").trim();
                        String cara = parts[1].trim();
                        tvBahan.setText(bahan);
                        tvCara.setText(cara);
                    } else if (rawText != null) {
                        tvBahan.setText(rawText);
                        tvCara.setText("");
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    tvBahan.setText("Gagal memuat bahan.");
                    tvCara.setText("Gagal memuat cara masak.");
                    Toast.makeText(RecipeDetailActivity.this, "Koneksi AI Terganggu", Toast.LENGTH_SHORT).show();
                });
            }
        }, Executors.newSingleThreadExecutor());
    }
}