package com.example.a1;

public class ThemeColor {
    private final String name;
    private final int drawableRes;

    public ThemeColor(String name, int drawableRes) {
        this.name = name;
        this.drawableRes = drawableRes;
    }

    public String getName() {
        return name;
    }

    public int getDrawableRes() {
        return drawableRes;
    }
}