package com.example.hellonutri;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<ChatMessage> historyList;

    public HistoryAdapter(List<ChatMessage> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage msg = historyList.get(position);
        holder.tvText.setText(msg.message); // Menampilkan pesan pertama sebagai judul sesi

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());
        holder.tvDate.setText(sdf.format(new Date(msg.timestamp)));

        // KLIK UNTUK LANJUTKAN CHAT
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChatActivity.class);
            intent.putExtra("SESSION_ID", msg.sessionId); // Kirim ID Sesi
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvText, tvDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvHistoryText);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
        }
    }
}