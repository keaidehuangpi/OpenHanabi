package me.theresa.fontRenderer.font.impl;

import me.theresa.fontRenderer.font.Color;

public interface Font {

    int getWidth(String str);

    int getHeight(String str);

    int getLineHeight();

    void drawString(float x, float y, String text);

    void drawString(float x, float y, String text, Color col);

    void drawString(float x, float y, String text, Color col, int startIndex, int endIndex);
}