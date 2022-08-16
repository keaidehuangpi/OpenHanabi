
package me.theresa.fontRenderer.font.effect;

import me.theresa.fontRenderer.font.Glyph;
import me.theresa.fontRenderer.font.UnicodeFont;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface Effect {
    void draw(BufferedImage image, Graphics2D g, UnicodeFont unicodeFont, Glyph glyph);
}
