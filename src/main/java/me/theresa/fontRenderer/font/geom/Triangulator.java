package me.theresa.fontRenderer.font.geom;

import java.io.Serializable;

public interface Triangulator extends Serializable {

    int getTriangleCount();

    float[] getTrianglePoint(int tri, int i);

    void addPolyPoint(float x, float y);

    void startHole();

    boolean triangulate();
}
