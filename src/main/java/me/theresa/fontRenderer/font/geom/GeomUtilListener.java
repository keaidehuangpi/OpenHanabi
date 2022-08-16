package me.theresa.fontRenderer.font.geom;


public interface GeomUtilListener {

    void pointExcluded(float x, float y);


    void pointIntersected(float x, float y);


    void pointUsed(float x, float y);
}
