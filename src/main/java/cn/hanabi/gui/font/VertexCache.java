package cn.hanabi.gui.font;

import org.lwjgl.opengl.GL11;

public class VertexCache {

    private int list;
    private long lastUsed;
    private char char_;
    private int width;

    public VertexCache(char char__, int list_, int width_) {
        char_ = char__;
        list = list_;
        width = width_;
    }

    public char getChar() {
        return char_;
    }

    public int getWidth() {
        return width;
    }

    public void render() {
        GL11.glCallList(list);
        GL11.glCallList(list); // FIXME: Why it needs to be called twice?
        lastUsed = System.currentTimeMillis();
    }

    public boolean checkTimeNotUsed(final long time) {
        return System.currentTimeMillis() - lastUsed > time;
    }

    public void destroy() {
        GL11.glDeleteLists(list, 1);
    }
}
