package com.example.hellonutri;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;

public class RecipeActivity extends AppCompatActivity {

    private TextView tvGreeting, tvBmrSub;
    private CardView cardSarapan, cardSiang, cardMalam;
    private TextView tvTitleSarapan, tvKcalSarapan, tvDescSarapan;
    private TextView tvTitleSiang, tvKcalSiang, tvDescSiang;
    private TextView tvTitleMalam, tvKcalMalam, tvDescMalam;
    private ImageView ivSarapan, ivSiang, ivMalam;
    private AppCompatButton btnRegenerate;
    private View btnBack;
    private TextView btnDiet, btnProtein, btnSimple;

    private GenerativeModelFutures model;
    private String kategoriAktif = "Diet";
    private String urlImgSarapan = "", urlImgSiang = "", urlImgMalam = "";

    // API Keys disembunyikan menggunakan BuildConfig
    private static final String PEXELS_API_KEY = BuildConfig.PEXELS_API_KEY;
    private static final String GEMINI_API_KEY = BuildConfig.GEMINI_API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash-lite", GEMINI_API_KEY);
        model = GenerativeModelFutures.from(gm);

        initViews();
        setupUserBmr();
        setupFilterLogic();
        setupDetailNavigation();

        btnBack.setOnClickListener(v -> finish());
        btnRegenerate.setOnClickListener(v -> generateRecipeWithAI());
        generateRecipeWithAI();
    }

    private void initViews() {
        tvGreeting = findViewById(R.id.tvRecipeGreeting);
        tvBmrSub = findViewById(R.id.tvRecipeSub);
        cardSarapan = findViewById(R.id.cardSarapan);
        cardSiang = findViewById(R.id.cardMakanSiang);
        cardMalam = findViewById(R.id.cardMakanMalam);
        tvTitleSarapan = findViewById(R.id.tvTitleSarapan);
        tvKcalSarapan = findViewById(R.id.tvKcalSarapan);
        tvDescSarapan = findViewById(R.id.tvDescSarapan);
        ivSarapan = findViewById(R.id.ivSarapan);
        tvTitleSiang = findViewById(R.id.tvTitleSiang);
        tvKcalSiang = findViewById(R.id.tvKcalSiang);
        tvDescSiang = findViewById(R.id.tvDescSiang);
        ivSiang = findViewById(R.id.ivSiang);
        tvTitleMalam = findViewById(R.id.tvTitleMalam);
        tvKcalMalam = findViewById(R.id.tvKcalMalam);
        tvDescMalam = findViewById(R.id.tvDescMalam);
        ivMalam = findViewById(R.id.ivMalam);
        btnDiet = findViewById(R.id.btnDiet);
        btnProtein = findViewById(R.id.btnProtein);
        btnSimple = findViewById(R.id.btnSimple);
        btnRegenerate = findViewById(R.id.btnRegenerate);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupUserBmr() {
        SharedPreferences pref = getSharedPreferences("HelloNutriPrefs", MODE_PRIVATE);
        String name = pref.getString("name", "User");
        tvGreeting.setText("Hai, " + name + "! 👋");
        tvBmrSub.setText("Menu sehat sesuai kebutuhan kalorimu hari ini.");
    }

    private void setupFilterLogic() {
        btnDiet.setOnClickListener(v -> { kategoriAktif = "Diet"; generateRecipeWithAI(); });
        btnProtein.setOnClickListener(v -> { kategoriAktif = "Tinggi Protein"; generateRecipeWithAI(); });
        btnSimple.setOnClickListener(v -> { kategoriAktif = "Simpel & Murah"; generateRecipeWithAI(); });
    }

    private void setupDetailNavigation() {
        View.OnClickListener listener = v -> {
            String judul = "", kkal = "", imgUrl = "";

            if (v.getId() == R.id.cardSarapan) {
                judul = tvTitleSarapan.getText().toString();
                kkal = tvKcalSarapan.getText().toString();
                imgUrl = urlImgSarapan;
            } else if (v.getId() == R.id.cardMakanSiang) {
                judul = tvTitleSiang.getText().toString();
                kkal = tvKcalSiang.getText().toString();
                imgUrl = urlImgSiang;
            } else if (v.getId() == R.id.cardMakanMalam) {
                judul = tvTitleMalam.getText().toString();
                kkal = tvKcalMalam.getText().toString();
                imgUrl = urlImgMalam;
            }

            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra("JUDUL", judul);
            intent.putExtra("KKAL", kkal);
            intent.putExtra("URL_GAMBAR", imgUrl);
            startActivity(intent);
        };
        cardSarapan.setOnClickListener(listener);
        cardSiang.setOnClickListener(listener);
        cardMalam.setOnClickListener(listener);
    }

    private void generateRecipeWithAI() {
        btnRegenerate.setText("⏳ Meracik Menu...");
        btnRegenerate.setEnabled(false);

        String prompt = "Berikan 3 menu sehat harian (Sarapan, Siang, Malam) kategori " + kategoriAktif + ". " +
                "Format WAJIB: NAMA|KKAL|DESC|KEYWORD_INGGRIS#NAMA|KKAL|DESC|KEYWORD_INGGRIS#NAMA|KKAL|DESC|KEYWORD_INGGRIS. " +
                "Contoh: Bubur Ayam|300 kcal|Bubur nasi sehat|chicken porridge. " +
                "KEYWORD_INGGRIS maksimal 2 kata bahasa Inggris untuk search foto makanan. " +
                "Hanya teks saja tanpa penjelasan tambahan.";

        Content content = new Content.Builder().addText(prompt).build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                runOnUiThread(() -> {
                    btnRegenerate.setText("Buat Resep Lagi ↻");
                    btnRegenerate.setEnabled(true);
                    String text = result.getText();
                    if (text != null && text.contains("#")) {
                        try {
                            String[] menus = text.trim().split("#");
                            if (menus.length >= 3) {
                                updateUI(menus[0].trim(), tvTitleSarapan, tvKcalSarapan, tvDescSarapan, ivSarapan);
                                updateUI(menus[1].trim(), tvTitleSiang, tvKcalSiang, tvDescSiang, ivSiang);
                                updateUI(menus[2].trim(), tvTitleMalam, tvKcalMalam, tvDescMalam, ivMalam);
                            }
                        } catch (Exception e) {
                            Toast.makeText(RecipeActivity.this, "Format AI salah, coba lagi.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    btnRegenerate.setEnabled(true);
                    btnRegenerate.setText("Buat Resep Lagi ↻");
                    Toast.makeText(RecipeActivity.this, "Koneksi Bermasalah: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }, Executors.newSingleThreadExecutor());
    }

    private void updateUI(String data, TextView title, TextView kcal, TextView desc, ImageView img) {
        String[] s = data.split("\\|");
        if (s.length >= 4) {
            title.setText(s[0].trim());
            kcal.setText(s[1].trim());
            desc.setText(s[2].trim());
            fetchImageFromPexels(s[3].trim(), img);
        }
    }

    private void fetchImageFromPexels(String keyword, ImageView img) {
        new Thread(() -> {
            try {
                String encodedKeyword = keyword.replace(" ", "%20");
                String apiUrl = "https://api.pexels.com/v1/search?query=" + encodedKeyword + "%20food&per_page=1&orientation=square";

                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Authorization", PEXELS_API_KEY);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                String response = sb.toString();
                String imageUrl = "";
                int mediumIndex = response.indexOf("\"medium\":\"");
                if (mediumIndex != -1) {
                    int start = mediumIndex + 10;
                    int end = response.indexOf("\"", start);
                    imageUrl = response.substring(start, end);
                }

                final String finalUrl = imageUrl;
                runOnUiThread(() -> {
                    if (img == ivSarapan) urlImgSarapan = finalUrl;
                    else if (img == ivSiang) urlImgSiang = finalUrl;
                    else if (img == ivMalam) urlImgMalam = finalUrl;

                    if (!finalUrl.isEmpty()) {
                        RequestOptions options = new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .error(android.R.drawable.ic_menu_report_image)
                                .centerCrop();

                        Glide.with(RecipeActivity.this)
                                .load(finalUrl)
                                .apply(options)
                                .into(img);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}