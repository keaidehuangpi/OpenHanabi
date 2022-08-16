package cn.hanabi.gui.font;

import cn.hanabi.utils.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;

public class VertexFontRenderer {

    private final Font font;
    private final FontMetrics fontMetrics;
    public final int fontHeight;

    private Map<Character, VertexCache> charMap = new HashMap<>();

    public VertexFontRenderer(Font font) {
        this.font = font;
        fontMetrics = new Canvas().getFontMetrics(font);
        fontHeight = ((fontMetrics.getHeight() < 0 ? font.getSize() : fontMetrics.getHeight() + 3) - 8) / 2;
    }

    public void drawString(String text, float x, float y, int color) {
        drawString(text, x, y, color, true);
    }

    public void drawString(String text, float x, float y, int color, boolean matrix) {
        if (matrix) {
            GL11.glPushMatrix();
        }

        GL11.glScalef(0.5F, 0.5F, 0.5F);
        GL11.glTranslated(x, y, 0.0);
        RenderUtil.color(color);

        for(char c : text.toCharArray()) {
            GL11.glTranslatef(drawChar(c), 0, 0);
        }

        if(matrix){
            GL11.glPopMatrix();
        }
    }

    public int drawChar(char c) {
        if(charMap.containsKey(c)) {
            VertexCache vc = charMap.get(c);
            vc.render();
            return vc.getWidth();
        }

        String charAsString = String.valueOf(c);
        int list = GL11.glGenLists(1);
        int width = fontMetrics.stringWidth(charAsString);
        GL11.glNewList(list, GL11.GL_COMPILE_AND_EXECUTE);
        RenderUtil.drawAWTShape(font.createGlyphVector(new FontRenderContext(new AffineTransform(), true, false),
                charAsString).getOutline(0, fontMetrics.getAscent()), 0.5);
        GL11.glEndList();
        charMap.put(c, new VertexCache(c, list, width));
        return width;
    }

    public int getStringWidth(String text) {
        return fontMetrics.stringWidth(text) / 2;
    }

    public void gcTick() {
        for (VertexCache cache : charMap.values().toArray(new VertexCache[0])) {
            if (cache.checkTimeNotUsed(FontsGC.REMOVE_TIME)) {
                cache.destroy();
                charMap.remove(cache.getChar());
            }
        }
    }

    public void destroy() {
        for (VertexCache cache : charMap.values()) {
            cache.destroy();
        }
        charMap.clear();
    }

    public void preGlHint() {
        GlStateManager.enableColorMaterial();
        GlStateManager.enableAlpha();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_FASTEST);
        GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public void postGlHint() {
        GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
        GL11.glEnable(GL11.GL_CULL_FACE);
//        GL11.glPopAttrib();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    public Font getFont() {
        return font;
    }
}
