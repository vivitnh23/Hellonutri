package com.example.hellonutri;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class ResultScanActivity extends AppCompatActivity {

    private GenerativeModelFutures model;
    private ImageView ivResultFood;
    private TextView tvFoodName, tvCalories, tvProtein, tvFat, tvCarbs;
    private AppDatabase db;

    // API Key disembunyikan menggunakan BuildConfig
    private final String API_KEY = BuildConfig.GEMINI_API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_scan);

        db = AppDatabase.getInstance(this);

        // 1. Inisialisasi View
        ivResultFood = findViewById(R.id.ivResultFood);
        tvFoodName = findViewById(R.id.tvFoodNameResult);
        tvCalories = findViewById(R.id.tvCaloriesResult);
        tvProtein = findViewById(R.id.tvProteinResult);
        tvFat = findViewById(R.id.tvFatResult);
        tvCarbs = findViewById(R.id.tvCarbsResult);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // 2. Setup AI Gemini
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash-lite", API_KEY);
        model = GenerativeModelFutures.from(gm);

        // 3. Tangkap foto dari intent
        Bitmap foto = getIntent().getParcelableExtra("FOTO_SCAN");
        if (foto != null) {
            ivResultFood.setImageBitmap(foto);
            analisaFoto(foto);
        } else {
            Toast.makeText(this, "Foto tidak ditemukan", Toast.LENGTH_SHORT).show();
        }

        // 4. Tombol Minta Versi Sehat -> Sekarang mengarah ke HealthyVersionActivity
        findViewById(R.id.btnHealthyVersion).setOnClickListener(v -> {
            Intent intent = new Intent(ResultScanActivity.this, HealthyVersionActivity.class);
            intent.putExtra("FOOD_NAME", tvFoodName.getText().toString());
            if (foto != null) {
                intent.putExtra("FOTO_SCAN", foto); // Mengirim data foto untuk ditampilkan di halaman sebelah
            }
            startActivity(intent);
        });

        // 5. Tombol Simpan
        findViewById(R.id.btnSaveHistory).setOnClickListener(v -> {
            saveScanResult();
        });
    }

    private void saveScanResult() {
        String name = tvFoodName.getText().toString();
        String cal = tvCalories.getText().toString();
        String pro = tvProtein.getText().toString();
        String fat = tvFat.getText().toString();
        String carb = tvCarbs.getText().toString();
        
        if (name.isEmpty() || name.equals("Menganalisa...") || name.equals("Gagal Analisa")) {
            Toast.makeText(this, "Data belum siap disimpan", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentDate = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date());

        SavedScan scan = new SavedScan(name, cal, pro, carb, fat, null, "", currentDate);
        db.savedDao().insertScan(scan);

        Toast.makeText(this, "Berhasil disimpan ke riwayat!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void analisaFoto(Bitmap foto) {
        tvFoodName.setText("Menganalisa...");
        // Prompt diperketat agar Gemini disiplin hanya mengeluarkan format JSON murni tanpa teks markdown
        String prompt = "Analisa makanan ini. Berikan estimasi nutrisinya. " +
                "Balas HANYA dengan format JSON murni tanpa markdown ataupun penjelasan lain: " +
                "{\"name\":\"nama makanan\",\"cal\":\"xxx kcal\",\"pro\":\"xx g\",\"fat\":\"xx g\",\"carb\":\"xx g\"}";

        Content content = new Content.Builder().addImage(foto).addText(prompt).build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                runOnUiThread(() -> {
                    try {
                        String rawText = result.getText();
                        if (rawText == null) {
                            throw new Exception("Response kosong");
                        }

                        // Membersihkan pembungkus markdown ```json ... ``` dari server Gemini
                        String jsonStr = rawText.trim();
                        if (jsonStr.startsWith("```json")) {
                            jsonStr = jsonStr.substring(7);
                        } else if (jsonStr.startsWith("```")) {
                            jsonStr = jsonStr.substring(3);
                        }
                        if (jsonStr.endsWith("```")) {
                            jsonStr = jsonStr.substring(0, jsonStr.length() - 3);
                        }

                        // Menghapus baris baru dan spasi berlebih
                        jsonStr = jsonStr.replaceAll("\\r|\\n", "").trim();

                        // Parsing data ke dalam komponen UI
                        JSONObject json = new JSONObject(jsonStr);
                        tvFoodName.setText(json.optString("name", "Tidak Dikenal"));
                        tvCalories.setText(json.optString("cal", "-"));
                        tvProtein.setText(json.optString("pro", "-"));
                        tvFat.setText(json.optString("fat", "-"));
                        tvCarbs.setText(json.optString("carb", "-"));

                    } catch (Exception e) {
                        e.printStackTrace();
                        tvFoodName.setText("Gagal Analisa");
                        Toast.makeText(ResultScanActivity.this, "Gagal memproses format data AI", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    tvFoodName.setText("Koneksi Error");
                    Toast.makeText(ResultScanActivity.this, "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }, Executors.newSingleThreadExecutor());
    }
}