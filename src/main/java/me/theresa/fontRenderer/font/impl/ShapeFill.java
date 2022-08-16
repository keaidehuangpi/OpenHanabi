package me.theresa.fontRenderer.font.impl;

import me.theresa.fontRenderer.font.Color;
import me.theresa.fontRenderer.font.geom.Shape;
import me.theresa.fontRenderer.font.geom.Vector2f;

public interface ShapeFill {

    Color colorAt(Shape shape, float x, float y);

    Vector2f getOffsetAt(Shape shape, float x, float y);
}
