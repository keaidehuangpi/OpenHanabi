package me.yarukon.palette;

import cn.hanabi.utils.MathUtils;
import cn.hanabi.utils.RenderUtil;

import java.awt.*;
import java.util.ArrayList;

public class ColorValue {
    public static final ArrayList<ColorValue> colorValues = new ArrayList<>();

    public String name;
    public float hue;
    public float saturation;
    public float brightness;
    public float alpha;
    public boolean hasAlpha;

    //Rainbow
    public boolean rainbow;
    public float rainbowSpeed;

    public ColorValue(String name, float hue, float saturation, float brightness, float alpha, boolean hasAlpha, boolean rainbow, float rainbowSpeed) {
        this.name = name;
        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
        this.alpha = alpha;
        this.hasAlpha = hasAlpha;
        this.rainbow = rainbow;
        this.rainbowSpeed = rainbowSpeed;

        colorValues.add(this);
    }

    public int getColor() {
        return getColor(0, true);
    }

    public int getColor(long timeOffsets, boolean alpha) {
        if (rainbow) {
            hue = ((float) (Math.ceil(System.currentTimeMillis() / (15.1 - rainbowSpeed) + timeOffsets) % 360f / 360f));
        }

        int color = Color.getHSBColor(this.hue, this.saturation, this.brightness).getRGB();
        return this.hasAlpha ? (alpha ? RenderUtil.reAlpha(color, MathUtils.clampValue(this.alpha, 0, 1)) : color) : color;
    }

    public static ColorValue getColorValueByName(String name) {
        for(ColorValue v : colorValues) {
            if(v.name.equals(name)) {
                return v;
            }
        }

        return null;
    }
}
