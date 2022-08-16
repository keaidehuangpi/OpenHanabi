package cn.hanabi.gui.newclickui.impl;

import cn.hanabi.Hanabi;
import cn.hanabi.gui.font.compat.WrappedVertexFontRenderer;
import cn.hanabi.gui.font.noway.ttfr.HFontRenderer;
import cn.hanabi.gui.newclickui.ClickUI;
import cn.hanabi.utils.RenderUtil;
import cn.hanabi.utils.TranslateUtil;
import cn.hanabi.value.Value;
import me.yarukon.font.GlyphPageFontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class DoubleValue {
    public Value values;

    public float length, x, y;

    public String name;
    public boolean premouse;

    public TranslateUtil anima = new TranslateUtil(0, 0);

    public DoubleValue(Value values) {
        this.values = values;
        this.name = values.getValueName().split("_")[1];
    }

    public void draw(float x, float y, float mouseX, float mouseY) {
        this.x = x;
        this.y = y;
        length = 30;
        HFontRenderer font = Hanabi.INSTANCE.fontManager.wqy18;
        font.drawString(name, x + 20, y + 8, new Color(255, 255, 255, 255).getRGB());
        double inc = values.getSteps();
        double max = (double) values.getValueMax();
        double min = (double) values.getValueMin();
        double valn = (double) values.getValueState();
        int longValue = 220 - 130;
        anima.interpolate((float) (longValue * (valn - min) / (max - min)), 0, 0.4f);

        RenderUtil.drawRoundRect(x + 130, y + 11, x + 220, y + 13, 1, new Color(70, 70, 70, 255).getRGB());
        RenderUtil.drawRoundRect(x + 130, y + 11, x + 130 + anima.getX(), y + 13, 1, new Color(255, 255, 255, 255).getRGB());

//		RenderUtil.drawRect(x + 130, y + 11, x + 220, y + 13, Colors.GREY.c);
//		RenderUtil.drawRect(x + 130, y + 11, x + 130 + anima.getX(), y + 13, ClickUI.color);
        RenderUtil.circle(x + 130 + anima.getX(), y + 12, 2, new Color(255, 255, 255, 255).getRGB());
        if (ClickUI.isHover(mouseX, mouseY, x + 120, y + 2, x + 230, y + length - 2)) {
            double v = longValue * (valn - min) / (max - min);
            font.drawCenteredString((double) values.getValueState() + "", (float) (x + 125 + v + 4), y, new Color(255, 255, 255, 255).getRGB());
        }
    }

    public void handleMouseinRender(float mouseX, float mouseY, int key) {
        if (ClickUI.isHover(mouseX, mouseY, x + 120, y + 2, x + 230, y + length - 2)) {
            if (Mouse.isButtonDown(0) && premouse) {
                double inc = values.getSteps();
                double max = (double) values.getValueMax();
                double min = (double) values.getValueMin();
                double valn = (double) values.getValueState();
                int longValue = 220 - 130;
                double valAbs = mouseX - (x + 130);
                double perc = valAbs / ((longValue) * Math.max(Math.min(valn / max, 0), 1));
                perc = Math.min(Math.max(0, perc), 1);
                double valRel = (max - min) * perc;
                double val = min + valRel;
                val = Math.round(val * (1 / inc)) / (1 / inc);
                values.setValueState(val);
            }
        }
    }

    public void handleMouse(float mouseX, float mouseY, int key) {
        if (ClickUI.isHover(mouseX, mouseY, x + 120, y + 2, x + 230, y + length - 2)) {
            if (key == 0) {
                premouse = true;
            }
        } else {
            premouse = false;
        }
    }

    public float getLength() {
        return this.length;
    }

    public void drawGradientSideways(float left, float top, float right, float bottom, int startColor, int endColor) {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos((double) right, (double) top, (double) 0).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos((double) left, (double) top, (double) 0).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos((double) left, (double) bottom, (double) 0).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos((double) right, (double) bottom, (double) 0).color(f5, f6, f7, f4).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public void drawGradientRect(double left, double top, double right, double bottom, boolean sideways, int startColor, int endColor) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_QUADS);
        RenderUtil.color(startColor);
        if (sideways) {
            GL11.glVertex2d(left, top);
            GL11.glVertex2d(left, bottom);
            RenderUtil.color(endColor);
            GL11.glVertex2d(right, bottom);
            GL11.glVertex2d(right, top);
        } else {
            GL11.glVertex2d(left, top);
            RenderUtil.color(endColor);
            GL11.glVertex2d(left, bottom);
            GL11.glVertex2d(right, bottom);
            RenderUtil.color(startColor);
            GL11.glVertex2d(right, top);
        }
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static int getColor(int color) {
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;
        int a = 255;
        return (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF | (a & 0xFF) << 24;
    }
}
