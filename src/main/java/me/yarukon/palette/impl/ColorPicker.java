package me.yarukon.palette.impl;

import cn.hanabi.Hanabi;
import cn.hanabi.utils.RenderUtil;
import me.yarukon.YRenderUtil;
import me.yarukon.palette.ColorValue;
import me.yarukon.palette.PaletteGui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ColorPicker {
    public PaletteGui parent;
    public ColorValue colorValue;

    public float x;
    public float y;
    public float width;
    public float height;


    public boolean sbDragging;
    public float sbPosX;
    public float sbPosY;

    public boolean rainbowSpeedDragging;
    public float rainbowSpeedPos = 0;

    public boolean hueDragging;
    public float huePos = 0;

    public boolean alphaDragging;
    public float alphaPos = 0;

    public ColorPicker(PaletteGui parent, ColorValue value) {
        this.parent = parent;
        this.colorValue = value;
    }

    public void render(float x, float y, float width, float height, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        // Name and Preview
        YRenderUtil.drawRect(x, y, width, height, 0xff2f3136);
        Hanabi.INSTANCE.fontManager.usans16.drawString(this.colorValue.name, x + 3f, y + 3f, 0xffffffff);

        int color = this.colorValue.getColor();
        int hueShit = Color.HSBtoRGB(this.colorValue.hue, 1, 1);
        YRenderUtil.drawRect(x + width - 24, y + 2f, 20, 10, 0xff454545);

        if (this.colorValue.hasAlpha) {
            drawCheckeredBackground(x + width - 24, y + 2f, x + width - 4, y + 12f);
        }

        YRenderUtil.drawRect(x + width - 24, y + 2f, 20, 10, color);

        // Toggle Rainbow button
        Hanabi.INSTANCE.fontManager.usans16.drawString("Rainbow", x + 3f, y + 16f, 0xffffffff);
        YRenderUtil.drawRect(x + width - 14, y + 16f, 10, 10, 0xff454545);

        if (this.colorValue.rainbow) {
            YRenderUtil.drawRect(x + width - 13, y + 17, 8, 8, 0xff00af87);
        }

        // Rainbow speed slider
        Hanabi.INSTANCE.fontManager.usans16.drawString("Speed: " + (Math.round(this.colorValue.rainbowSpeed * 10d) / 10d), x + 3f, y + 29f, 0xffffffff);
        YRenderUtil.drawRect(x + width - 104, y + 32f, 100, 3, 0xff454545);

        this.dealWithRenderPos(0);
        YRenderUtil.drawRect(x + width - 104, y + 32f, rainbowSpeedPos, 3, 0xff00af87);
        YRenderUtil.drawRect(x + width - 104 - 3f + rainbowSpeedPos, y + 32f - 1.5f, 6, 6, 0xff00af87);

        if (rainbowSpeedDragging) {
            this.dealWithDragging(mouseX, mouseY, 0);
        }

        // Saturation and Brightness
        // YRenderUtil.drawRect(x + 4f, y + 44f, width - 8, 60f, Colors.BLUE.c);

        dealWithRenderPos(1);
        dealWithRenderPos(2);

        if (this.sbDragging) {
            dealWithDragging(mouseX, mouseY, 1);
            dealWithDragging(mouseX, mouseY, 2);
        }

        drawGradientRect(x + 4f, y + 44f, x + width - 4, y + 104f, true, 0xffffffff, hueShit);
        drawGradientRect(x + 4f, y + 44f, x + width - 4, y + 110.5f, false, getColor(0), 0);
        RenderUtil.drawRect(x + 4f + sbPosX - 2f, y + 44f + sbPosY - 2f, x + 4f + sbPosX + 2f, y + 44f + sbPosY + 2f, 0xff000000);
        RenderUtil.drawRect(x + 4f + sbPosX - 1f, y + 44f + sbPosY - 1f, x + 4f + sbPosX + 1f, y + 44f + sbPosY + 1f, 0xffffffff);

        // Hue
        // YRenderUtil.drawRect(x + 4f, y + 108f, width - 8, 12f, Colors.BLUE.c);
        float sliderLeft = x + width - 4;
        float sliderRight = x + 4f;
        float hueSliderWidth = sliderLeft - sliderRight;
        float hueSelector = hueSliderWidth / 5f;
        float asLeft = sliderLeft;
        int i = 0;
        while (i < 5) {
            boolean last = i == 4;
            drawGradientSideways(asLeft - hueSelector, y + 108f, asLeft + hueSelector - hueSelector, y + 120f, getColor(Color.HSBtoRGB(1 - 0.2f * (i + 1), 1.0f, 1.0f)), getColor(Color.HSBtoRGB(1 - 0.2f * i, 1.0f, 1.0f)));
            if (!last) {
                asLeft -= hueSelector;
            }
            ++i;
        }

        dealWithRenderPos(3);
        if (this.hueDragging) {
            this.dealWithDragging(mouseX, mouseY, 3);
        }
        YRenderUtil.drawRect(x + 2f + (huePos < 2 ? 2 : Math.min(huePos, width - 8)), y + 108f, 2f, 12f, 0xff000000);

        // Alpha
        // YRenderUtil.drawRect(x + 4f, y + 124f, width - 8, 12f, Colors.BLUE.c);

        dealWithRenderPos(4);
        drawCheckeredBackground(x + 4f, y + 124f, x + width - 4, y + 124f + 12f);
        this.drawGradientSideways(x + 4f, y + 124f, x + width - 4, y + 124f + 12f, RenderUtil.reAlpha(hueShit, 0), hueShit);
        YRenderUtil.drawRect(x + 2f + (alphaPos < 2 ? 2 : Math.min(alphaPos, width - 8)), y + 124f, 2f, 12f, 0xff000000);

        if (!this.colorValue.hasAlpha) {
            YRenderUtil.drawRect(x + 4f, y + 124f, width - 8, 12f, 0xbb404040);
            Hanabi.INSTANCE.fontManager.usans16.drawCenteredString("No support for alpha", x + 4 + ((width - 8) / 2f), y + 126f, 0xffffffff);
        }

        if (alphaDragging && this.colorValue.hasAlpha) {
            this.dealWithDragging(mouseX, mouseY, 4);
        }
    }

    public void dealWithRenderPos(int type) {
        float curVal;
        float minVal;
        float maxVal;
        float barLength;
        switch (type) {
            case 0: //Rainbow speed
                curVal = this.colorValue.rainbowSpeed;
                minVal = 5f;
                maxVal = 15f;
                barLength = 100;
                rainbowSpeedPos = barLength * (curVal - minVal) / (maxVal - minVal);
                break;

            case 1: //Saturation
                curVal = this.colorValue.saturation;
                minVal = 0f;
                maxVal = 1f;
                barLength = width - 8;

                sbPosX = barLength * (curVal - minVal) / (maxVal - minVal);
                break;

            case 2: //Brightness
                curVal = this.colorValue.brightness;
                minVal = 0f;
                maxVal = 1f;
                barLength = 60;

                sbPosY = barLength * (curVal - minVal) / (maxVal - minVal);
                break;

            case 3: //Hue
                curVal = this.colorValue.hue;
                minVal = 0f;
                maxVal = 1f;
                barLength = width - 8;

                huePos = barLength * (curVal - minVal) / (maxVal - minVal);
                break;

            case 4: //Alpha
                curVal = this.colorValue.alpha;
                minVal = 0f;
                maxVal = 1f;
                barLength = width - 8;

                alphaPos = barLength * (curVal - minVal) / (maxVal - minVal);
                break;
        }
    }

    public void dealWithDragging(int mouseX, int mouseY, int type) {
        float curVal;
        float minVal;
        float maxVal;
        float step;
        float barLength;

        float valAbs;
        float perc;
        float valRel;
        float val;
        switch (type) {
            case 0: //Rainbow speed
                curVal = this.colorValue.rainbowSpeed;
                minVal = 5f;
                maxVal = 15f;
                step = 0.1f;
                barLength = 100;

                valAbs = mouseX - (x + width - 104);
                perc = valAbs / ((barLength) * Math.max(Math.min(curVal / maxVal, 0), 1));
                perc = Math.min(Math.max(0, perc), 1);
                valRel = (maxVal - minVal) * perc;
                val = minVal + valRel;
                this.colorValue.rainbowSpeed = Math.round(val * (1 / step)) / (1 / step);
                break;

            case 1: //Saturation
                curVal = this.colorValue.saturation;
                minVal = 0f;
                maxVal = 1f;
                step = 0.01f;
                barLength = width - 8;

                valAbs = mouseX - (x + 4f);
                perc = valAbs / ((barLength) * Math.max(Math.min(curVal / maxVal, 0), 1));
                perc = Math.min(Math.max(0, perc), 1);
                valRel = (maxVal - minVal) * perc;
                val = minVal + valRel;
                this.colorValue.saturation = Math.round(val * (1 / step)) / (1 / step);
                break;

            case 2: //Brightness
                curVal = this.colorValue.brightness;
                minVal = 0f;
                maxVal = 1f;
                step = 0.01f;
                barLength = 60;

                valAbs = mouseY - (y + 44f);
                perc = valAbs / ((barLength) * Math.max(Math.min(curVal / maxVal, 0), 1));
                perc = Math.min(Math.max(0, perc), 1);
                valRel = (maxVal - minVal) * perc;
                val = minVal + valRel;
                this.colorValue.brightness = Math.round(val * (1 / step)) / (1 / step);
                break;

            case 3: //Hue
                curVal = this.colorValue.hue;
                minVal = 0f;
                maxVal = 1f;
                step = 0.01f;
                barLength = width - 8;

                valAbs = mouseX - (x + 4f);
                perc = valAbs / ((barLength) * Math.max(Math.min(curVal / maxVal, 0), 1));
                perc = Math.min(Math.max(0, perc), 1);
                valRel = (maxVal - minVal) * perc;
                val = minVal + valRel;
                this.colorValue.hue = Math.round(val * (1 / step)) / (1 / step);
                break;

            case 4: //Alpha
                curVal = this.colorValue.alpha;
                minVal = 0f;
                maxVal = 1f;
                step = 0.01f;
                barLength = width - 8;

                valAbs = mouseX - (x + 4f);
                perc = valAbs / ((barLength) * Math.max(Math.min(curVal / maxVal, 0), 1));
                perc = Math.min(Math.max(0, perc), 1);
                valRel = (maxVal - minVal) * perc;
                val = minVal + valRel;
                this.colorValue.alpha = Math.round(val * (1 / step)) / (1 / step);
                break;
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (YRenderUtil.isHoveringBound(mouseX, mouseY, parent.x, parent.y, 200, 300) && y > parent.y + parent.draggableHeight && y < parent.y + parent.draggableHeight + 300) {
            if (YRenderUtil.isHoveringBound(mouseX, mouseY, x + width - 104 - 3f + rainbowSpeedPos, y + 32f - 1.5f, 6, 6) || YRenderUtil.isHoveringBound(mouseX, mouseY, x + width - 104, y + 32f, 100, 3)) {
                rainbowSpeedDragging = true;
            }

            if (YRenderUtil.isHoveringBound(mouseX, mouseY, x + 4f, y + 44f, width - 8, 60f)) {
                sbDragging = true;
            }

            if (YRenderUtil.isHoveringBound(mouseX, mouseY, x + 4f, y + 108f, width - 8, 12f)) {
                hueDragging = true;
            }

            if (YRenderUtil.isHoveringBound(mouseX, mouseY, x + 4f, y + 124f, width - 8, 12f)) {
                alphaDragging = true;
            }

            if (YRenderUtil.isHoveringBound(mouseX, mouseY, x + width - 14, y + 16f, 10, 10)) {
                this.colorValue.rainbow = !this.colorValue.rainbow;
            }
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        rainbowSpeedDragging = false;
        sbDragging = false;
        hueDragging = false;
        alphaDragging = false;
    }

    private static void drawCheckeredBackground(float left, float top, float right, float bottom) {
        YRenderUtil.drawRectNormal(left, top, right, bottom, 0xffffffff);
        for (boolean offset = false; top < bottom; ++top) {
            for (float x1 = left + (float) ((offset = !offset) ? 1 : 0); x1 < right; x1 += 2.0F) {
                if (x1 <= right - 1.0F) {
                    YRenderUtil.drawRectNormal(x1, top, x1 + 1.0F, top + 1.0F, 0xff808080);
                }
            }
        }
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
