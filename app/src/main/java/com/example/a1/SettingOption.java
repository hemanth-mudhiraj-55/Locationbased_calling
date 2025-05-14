package com.example.a1;

import android.view.View;

public class SettingOption {
    private String title;
    private int iconRes;
    private View.OnClickListener onClickListener;

    public SettingOption(String title, int iconRes, View.OnClickListener onClickListener) {
        this.title = title;
        this.iconRes = iconRes;
        this.onClickListener = onClickListener;
    }

    // Getters
    public String getTitle() { return title; }
    public int getIconRes() { return iconRes; }
    public View.OnClickListener getOnClickListener() { return onClickListener; }
}