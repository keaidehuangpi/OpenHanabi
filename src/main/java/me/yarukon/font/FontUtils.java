package me.yarukon.font;

import cn.hanabi.Hanabi;

import java.awt.*;
import java.io.InputStream;

public class FontUtils {

    public static Font getFont(String fontName, float size) {
        Font font;
        try {
            InputStream is = FontUtils.class.getResourceAsStream("/assets/minecraft/Client/fonts/" + fontName);
            font = Font.createFont(Font.PLAIN, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            System.out.println("Error while loading font " + fontName + " - " + size + "!");
            font = new Font("Arial", Font.PLAIN, (int) size);
        }

        return font;
    }
}
