package com.example.hellonutri;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class HealthyVersionActivity extends AppCompatActivity {

    private GenerativeModelFutures model;
    private TextView tvName, tvCalories, tvReason;
    private LinearLayout containerComposition;
    private ImageView ivFood;
    private AppDatabase db;
    private String compositionString = ""; 

    // API Key disembunyikan menggunakan BuildConfig
    private final String API_KEY = BuildConfig.GEMINI_API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthy_version);

        db = AppDatabase.getInstance(this);

        tvName = findViewById(R.id.tvHealthyName);
        tvCalories = findViewById(R.id.tvHealthyCalories);
        tvReason = findViewById(R.id.tvHealthyReason);
        containerComposition = findViewById(R.id.containerComposition);
        ivFood = findViewById(R.id.ivHealthyFood);

        findViewById(R.id.btnBackHealthy).setOnClickListener(v -> finish());

        findViewById(R.id.btnSaveHealthy).setOnClickListener(v -> {
            saveHealthyToHistory();
        });

        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash-lite", API_KEY);
        model = GenerativeModelFutures.from(gm);

        String originalFood = getIntent().getStringExtra("FOOD_NAME");
        Bitmap foto = getIntent().getParcelableExtra("FOTO_SCAN");

        if (foto != null) {
            ivFood.setImageBitmap(foto);
        }

        if (originalFood != null && !originalFood.isEmpty()) {
            getHealthyAnalysis(originalFood);
        } else {
            Toast.makeText(this, "Nama makanan tidak valid", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveHealthyToHistory() {
        String name = tvName.getText().toString();
        String cal = tvCalories.getText().toString();
        
        if (name.equals("Memuat...") || name.isEmpty()) {
            Toast.makeText(this, "Tunggu analisa selesai", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());

        SavedScan scan = new SavedScan(name, cal, "-", "-", "-", null, compositionString, currentDate);
        db.savedDao().insertScan(scan);

        Toast.makeText(this, "Berhasil disimpan ke Tersimpan!", Toast.LENGTH_SHORT).show();
        
        Intent intent = new Intent(this, SavedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void getHealthyAnalysis(String foodName) {
        String prompt = "Berikan versi sehat dari makanan: " + foodName + ". " +
                "Balas HANYA dengan JSON murni tanpa markdown: " +
                "{\"name\":\"... Versi Sehat\",\"cal\":\"... kcal\",\"reason\":\"...\",\"composition\":[\"item1\",\"item2\"]}";

        Content content = new Content.Builder().addText(prompt).build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                runOnUiThread(() -> {
                    try {
                        String rawText = result.getText();
                        String jsonStr = rawText.trim();
                        if (jsonStr.startsWith("```json")) jsonStr = jsonStr.substring(7);
                        if (jsonStr.endsWith("```")) jsonStr = jsonStr.substring(0, jsonStr.length() - 3);

                        JSONObject json = new JSONObject(jsonStr);

                        tvName.setText(json.optString("name", foodName + " Versi Sehat"));
                        tvCalories.setText(json.optString("cal", "0 kcal"));
                        tvReason.setText(json.optString("reason", ""));

                        JSONArray comp = json.optJSONArray("composition");
                        containerComposition.removeAllViews();
                        StringBuilder sb = new StringBuilder();

                        if (comp != null) {
                            for (int i = 0; i < comp.length(); i++) {
                                String item = comp.getString(i);
                                addChecklistItem(item);
                                sb.append("- ").append(item).append("\n");
                            }
                        }
                        compositionString = sb.toString();

                    } catch (Exception e) {
                        tvName.setText("Gagal memuat");
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> Toast.makeText(HealthyVersionActivity.this, "Koneksi Error", Toast.LENGTH_SHORT).show());
            }
        }, Executors.newSingleThreadExecutor());
    }

    private void addChecklistItem(String text) {
        View view = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_multiple_choice, null);
        TextView tv = view.findViewById(android.R.id.text1);
        if (tv != null) {
            tv.setText(text);
            tv.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.checkbox_on_background, 0, 0, 0);
            tv.setCompoundDrawablePadding(20);
            tv.setPadding(0, 10, 0, 10);
            containerComposition.addView(view);
        }
    }
}