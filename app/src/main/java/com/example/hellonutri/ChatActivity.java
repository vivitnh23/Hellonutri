package com.example.hellonutri;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.Executors;

import io.noties.markwon.Markwon;

public class ChatActivity extends AppCompatActivity {

    // API Key disembunyikan, diambil dari local.properties via BuildConfig
    private final String API_KEY = BuildConfig.GEMINI_API_KEY;

    private LinearLayout chatContent;
    private EditText etInputChat;
    private NestedScrollView chatScrollView;
    private GenerativeModelFutures model;
    private Markwon markwon;
    private BottomNavigationView bottomNavigation;
    private AppDatabase db;
    private long currentSessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        db = AppDatabase.getInstance(this);
        markwon = Markwon.create(this);

        chatContent = findViewById(R.id.chatContent);
        etInputChat = findViewById(R.id.etMessage);
        chatScrollView = findViewById(R.id.chatScrollView);
        bottomNavigation = findViewById(R.id.bottomNavigationChat);

        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash-lite", API_KEY);
        model = GenerativeModelFutures.from(gm);

        setupNavColorAndLogic();
        setupKeyboardListener();

        currentSessionId = getIntent().getLongExtra("SESSION_ID", -1);
        if (currentSessionId != -1) {
            loadExistingChat();
        } else {
            currentSessionId = System.currentTimeMillis();
        }

        findViewById(R.id.btnSend).setOnClickListener(v -> handleSend());
        findViewById(R.id.btnBackChat).setOnClickListener(v -> finish());

        checkIncomingIntent();
    }

    private void setupKeyboardListener() {
        final View rootView = findViewById(R.id.chatRoot);
        if (rootView == null) return;

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            int screenHeight = rootView.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) {
                bottomNavigation.setVisibility(View.GONE);
            } else {
                bottomNavigation.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadExistingChat() {
        List<ChatMessage> messages = db.chatDao().getMessagesBySession(currentSessionId);
        for (ChatMessage m : messages) {
            addMessageToUI(m.message, m.isUser, null);
        }
    }

    private void checkIncomingIntent() {
        if (getIntent().hasExtra("FOTO_SCAN")) {
            Bitmap foto = getIntent().getParcelableExtra("FOTO_SCAN");
            if (foto != null) {
                addMessage("Tolong analisa makanan di foto ini ya! 📸", true, foto);
                panggilAI("Analisa foto makanan ini: Sebutkan nama makanan, estimasi kalori, dan apakah sehat untuk diet?", foto);
            }
        }
    }

    private void handleSend() {
        String teks = etInputChat.getText().toString().trim();
        if (teks.isEmpty()) return;
        addMessage(teks, true, null);
        etInputChat.setText("");
        panggilAI(teks, null);
    }

    private void panggilAI(String prompt, @Nullable Bitmap foto) {
        View loading = LayoutInflater.from(this).inflate(R.layout.item_chat_ai, chatContent, false);
        TextView tvLoad = loading.findViewById(R.id.txtMessage);
        tvLoad.setText("⏳ Sedang menganalisa...");
        chatContent.addView(loading);

        String instruction = "Kamu adalah HelloNutri AI, asisten nutrisi profesional yang ramah. " +
                "TUGAS: Memberikan info kalori, tips diet sehat, dan saran makanan alternatif yang murah tapi bergizi. " +
                "GAYA BAHASA: Santai seperti teman, gunakan bahasa Indonesia yang mudah dimengerti. ";

        Content.Builder contentBuilder = new Content.Builder();
        if (foto != null) contentBuilder.addImage(foto);
        contentBuilder.addText(instruction + prompt);

        ListenableFuture<GenerateContentResponse> response = model.generateContent(contentBuilder.build());
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                runOnUiThread(() -> {
                    chatContent.removeView(loading);
                    addMessage(result.getText(), false, null);
                });
            }
            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    chatContent.removeView(loading);
                    Toast.makeText(ChatActivity.this, "Koneksi terputus!", Toast.LENGTH_SHORT).show();
                });
            }
        }, Executors.newSingleThreadExecutor());
    }

    private void addMessage(String teks, boolean isUser, @Nullable Bitmap foto) {
        db.chatDao().insert(new ChatMessage(currentSessionId, teks, isUser, System.currentTimeMillis()));
        addMessageToUI(teks, isUser, foto);
    }

    private void addMessageToUI(String teks, boolean isUser, @Nullable Bitmap foto) {
        View v = LayoutInflater.from(this).inflate(isUser ? R.layout.item_chat_user : R.layout.item_chat_ai, chatContent, false);
        TextView tv = v.findViewById(R.id.txtMessage);
        if (!isUser) markwon.setMarkdown(tv, teks);
        else tv.setText(teks);

        if (isUser && foto != null) {
            ImageView iv = v.findViewById(R.id.imgMessage);
            if (iv != null) {
                iv.setVisibility(View.VISIBLE);
                iv.setImageBitmap(foto);
            }
        }
        chatContent.addView(v);
        chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void setupNavColorAndLogic() {
        int[][] states = new int[][]{ new int[]{android.R.attr.state_checked}, new int[]{-android.R.attr.state_checked} };
        int[] colors = new int[]{ Color.parseColor("#4CAF50"), Color.parseColor("#888888") };
        ColorStateList colorList = new ColorStateList(states, colors);

        bottomNavigation.setItemIconTintList(colorList);
        bottomNavigation.setItemTextColor(colorList);
        bottomNavigation.setSelectedItemId(R.id.nav_chat);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_saved) {
                startActivity(new Intent(this, SavedActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return true;
        });
    }
}