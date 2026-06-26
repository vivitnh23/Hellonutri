package com.example.hellonutri;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class SavedScanAdapter extends RecyclerView.Adapter<SavedScanAdapter.ViewHolder> {

    private List<SavedScan> scanList;
    private Context context;

    public SavedScanAdapter(List<SavedScan> scanList) {
        this.scanList = scanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_saved_scan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavedScan scan = scanList.get(position);
        holder.tvFoodName.setText(scan.foodName);
        holder.tvCalories.setText(scan.calories);
        holder.tvMacros.setText("Protein " + scan.protein + " • Karbo " + scan.carbs + " • Lemak " + scan.fat);
        holder.tvDate.setText(scan.tanggalSimpan != null ? "Disimpan " + scan.tanggalSimpan : "Disimpan hari ini");

        Glide.with(context)
                .load(scan.imagePath)
                .placeholder(R.drawable.nasi)
                .into(holder.ivFood);

        holder.btnDetail.setOnClickListener(v -> {
            Intent intent = new Intent(context, ScanDetailActivity.class);
            intent.putExtra("ID", scan.id);
            intent.putExtra("NAME", scan.foodName);
            intent.putExtra("CALORIES", scan.calories);
            intent.putExtra("PROTEIN", scan.protein);
            intent.putExtra("CARBS", scan.carbs);
            intent.putExtra("FAT", scan.fat);
            intent.putExtra("KOMPOSISI", scan.komposisi);
            intent.putExtra("DATE", scan.tanggalSimpan);
            intent.putExtra("IMAGE_PATH", scan.imagePath);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return scanList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFood;
        TextView tvFoodName, tvCalories, tvMacros, tvDate;
        Button btnDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFood = itemView.findViewById(R.id.ivFood);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvCalories = itemView.findViewById(R.id.tvCalories);
            tvMacros = itemView.findViewById(R.id.tvMacros);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnDetail = itemView.findViewById(R.id.btnDetail);
        }
    }
}