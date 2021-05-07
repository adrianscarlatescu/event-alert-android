package com.as.eventalertandroid.handler;

import android.graphics.Color;

public class ColorHandler {

    public static int getColorFromHex(int hex) {
        String strColor = String.format("#%06X", 0xFFFFFF & hex);
        return Color.parseColor(strColor);
    }

    public static int getColorWithAlpha(int argb, float alpha) {
        return (argb & 0x00FFFFFF) | ((int) (alpha * 255.0f + 0.5f) << 24);
    }

    public static int getColorFromHex(int hex, float alpha) {
        return getColorWithAlpha(getColorFromHex(hex), alpha);
    }

}
