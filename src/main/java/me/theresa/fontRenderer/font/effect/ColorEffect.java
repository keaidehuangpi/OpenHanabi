
package me.theresa.fontRenderer.font.effect;

import me.theresa.fontRenderer.font.Glyph;
import me.theresa.fontRenderer.font.UnicodeFont;
import me.theresa.fontRenderer.font.util.EffectUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


public class ColorEffect implements ConfigurableEffect {
	
	private Color color = Color.white;

	
	public ColorEffect() {
	}

	
	public ColorEffect(Color color) {
		this.color = color;
	}

	
	public void draw(BufferedImage image, Graphics2D g, UnicodeFont unicodeFont, Glyph glyph) {
		g.setColor(color);
		g.fill(glyph.getShape());
	}

	
	public Color getColor() {
		return color;
	}

	
	public void setColor(Color color) {
		if (color == null) throw new IllegalArgumentException("color cannot be null.");
		this.color = color;
	}

	
	public String toString () {
		return "Color";
	}

	
	public List getValues() {
		List values = new ArrayList();
		values.add(EffectUtil.colorValue("Color", color));
		return values;
	}

	
	public void setValues(List values) {
		for (Object o : values) {
			Value value = (Value) o;
			if (value.getName().equals("Color")) {
				setColor((Color) value.getObject());
			}
		}
	}
}
