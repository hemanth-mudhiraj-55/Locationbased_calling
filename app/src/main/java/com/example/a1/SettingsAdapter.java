package com.example.a1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingViewHolder> {

    private final List<SettingOption> settingsOptions;

    public SettingsAdapter(List<SettingOption> settingsOptions) {
        this.settingsOptions = settingsOptions;
    }

    @NonNull
    @Override
    public SettingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_setting_option, parent, false);
        return new SettingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingViewHolder holder, int position) {
        SettingOption option = settingsOptions.get(position);
        holder.icon.setImageResource(option.getIconRes());
        holder.title.setText(option.getTitle());
        holder.itemView.setOnClickListener(option.getOnClickListener());
    }

    @Override
    public int getItemCount() {
        return settingsOptions.size();
    }

    static class SettingViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;

        public SettingViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.optionIcon);
            title = itemView.findViewById(R.id.optionTitle);
        }
    }
}