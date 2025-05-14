package com.example.a1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ThemeColorAdapter extends RecyclerView.Adapter<ThemeColorAdapter.ThemeColorViewHolder> {

    private final List<ThemeColor> themeColors;
    private final OnThemeColorSelectedListener listener;

    public ThemeColorAdapter(List<ThemeColor> themeColors, OnThemeColorSelectedListener listener) {
        this.themeColors = themeColors;
        this.listener = listener;
    }

    public interface OnThemeColorSelectedListener {
        void onThemeColorSelected(ThemeColor themeColor);
    }

    @NonNull
    @Override
    public ThemeColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_theme_color, parent, false);
        return new ThemeColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThemeColorViewHolder holder, int position) {
        ThemeColor themeColor = themeColors.get(position);
        holder.colorPreview.setImageResource(themeColor.getDrawableRes());
        holder.itemView.setOnClickListener(v -> listener.onThemeColorSelected(themeColor));
    }

    @Override
    public int getItemCount() {
        return themeColors.size();
    }

    static class ThemeColorViewHolder extends RecyclerView.ViewHolder {
        ImageView colorPreview;

        public ThemeColorViewHolder(@NonNull View itemView) {
            super(itemView);
            colorPreview = itemView.findViewById(R.id.colorPreview);
        }
    }
}