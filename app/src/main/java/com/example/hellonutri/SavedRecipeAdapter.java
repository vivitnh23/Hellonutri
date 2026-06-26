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

public class SavedRecipeAdapter extends RecyclerView.Adapter<SavedRecipeAdapter.ViewHolder> {

    private List<SavedRecipe> recipeList;
    private Context context;

    public SavedRecipeAdapter(List<SavedRecipe> recipeList) {
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_saved_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavedRecipe recipe = recipeList.get(position);
        holder.tvTitle.setText(recipe.title);
        holder.tvKcal.setText(recipe.kcal);
        holder.tvDate.setText(recipe.tanggalSimpan != null ? "Disimpan " + recipe.tanggalSimpan : "Disimpan hari ini");

        Glide.with(context)
                .load(recipe.imagePath)
                .placeholder(R.drawable.oatmeal)
                .into(holder.ivRecipe);

        holder.btnDetail.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra("ID", recipe.id); // Penting untuk fitur hapus
            intent.putExtra("JUDUL", recipe.title);
            intent.putExtra("KKAL", recipe.kcal);
            intent.putExtra("URL_GAMBAR", recipe.imagePath);
            intent.putExtra("BAHAN", recipe.bahan);
            intent.putExtra("CARA", recipe.cara);
            intent.putExtra("FROM_SAVED", true);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRecipe;
        TextView tvTitle, tvKcal, tvDate;
        Button btnDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRecipe = itemView.findViewById(R.id.ivRecipe);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvKcal = itemView.findViewById(R.id.tvKcal);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnDetail = itemView.findViewById(R.id.btnDetail);
        }
    }
}