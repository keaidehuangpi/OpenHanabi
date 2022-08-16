package cn.hanabi.gui.notifications;

import cn.hanabi.Hanabi;
import cn.hanabi.utils.ClientUtil;
import cn.hanabi.utils.Colors;
import cn.hanabi.utils.RenderUtil;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.utils.fontmanager.HanabiFonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Notification {

    private final Type t;
    private final long stayTime;
    public String message;
    public TimeHelper timer;
    private double lastY, posY, width, height, animationX;
    private int color, imageWidth;

    public Notification(String message, Type type) {
        this.message = message;
        timer = new TimeHelper();
        timer.reset();
        if (Minecraft.getMinecraft().thePlayer != null) {
            width = Hanabi.INSTANCE.fontManager.comfortaa16.getStringWidth(message) + 35;
        } else {
            width = 0;
        }
        height = 20;
        animationX = width;
        stayTime = 1200;
        imageWidth = 16;
        posY = -1;
        t = type;
        if (type.equals(Type.INFO))
            color = Colors.DARKGREY.c;
        else if (type.equals(Type.ERROR))
            color = new Color(36, 36, 36).getRGB();
        else if (type.equals(Type.SUCCESS))
            color = new Color(36, 36, 36).getRGB();
        else if (type.equals(Type.WARNING))
            color = Colors.DARKGREY.c;
    }

    public void draw(double getY, double lastY) {
        width = Hanabi.INSTANCE.fontManager.comfortaa15.getStringWidth(message) + 45;
        height = 22;
        imageWidth = 11;
        this.lastY = lastY;
        animationX = this.getAnimationState(animationX, isFinished() ? width : 0, Math.max(isFinished() ? 200 : 30, Math.abs(animationX - (isFinished() ? width : 0)) * 20) * 0.3);
        if (posY == -1)
            posY = getY;
        else
            posY = this.getAnimationState(posY, getY, 200);
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        int x1 = (int) (res.getScaledWidth() - width + animationX), x2 = (int) (res.getScaledWidth() + animationX), y1 = (int) posY - 22, y2 = (int) (y1 + height);

        RenderUtil.drawRect(x1, y1, x2, y2, ClientUtil.reAlpha(color, 0.85f));
        RenderUtil.drawRect(x1, y2 - 1, x1 + Math.min((x2 - x1) * (System.currentTimeMillis() - timer.getLastMs()) / stayTime, x2 - x1), y2, ClientUtil.reAlpha(-1, 0.85f));

        switch (t) {
            case ERROR:
                Hanabi.INSTANCE.fontManager.icon25.drawString(HanabiFonts.ICON_NOTIFY_ERROR, x1 + 3, y1 + 8, Colors.WHITE.c);
                break;
            case INFO:
                Hanabi.INSTANCE.fontManager.icon25.drawString(HanabiFonts.ICON_NOTIFY_INFO, x1 + 3, y1 + 8, Colors.WHITE.c);
                break;
            case SUCCESS:
                Hanabi.INSTANCE.fontManager.icon25.drawString(HanabiFonts.ICON_NOTIFY_SUCCESS, x1 + 3, y1 + 8, Colors.WHITE.c);
                break;
            case WARNING:
                Hanabi.INSTANCE.fontManager.icon25.drawString(HanabiFonts.ICON_NOTIFY_WARN, x1 + 3, y1 + 8, Colors.WHITE.c);
                break;
        }


        y1 += 1;
        if (message.contains(" Enabled")) {
            Hanabi.INSTANCE.fontManager.comfortaa15.drawString(message.replace(" Enabled", ""), (float) (x1 + 19), (float) (y1 + height / 4F), -1);
            Hanabi.INSTANCE.fontManager.comfortaa15.drawString(" Enabled", (float) (x1 + 20 + Hanabi.INSTANCE.fontManager.comfortaa15.getStringWidth(message.replace(" Enabled", ""))), (float) (y1 + height / 4F), Colors.GREY.c);
        } else if (message.contains(" Disabled")) {
            Hanabi.INSTANCE.fontManager.comfortaa15.drawString(message.replace(" Disabled", ""), (float) (x1 + 19), (float) (y1 + height / 4F), -1);
            Hanabi.INSTANCE.fontManager.comfortaa15.drawString(" Disabled", (float) (x1 + 20 + Hanabi.INSTANCE.fontManager.comfortaa15.getStringWidth(message.replace(" Disabled", ""))), (float) (y1 + height / 4F), Colors.GREY.c);
        } else {
            Hanabi.INSTANCE.fontManager.comfortaa15.drawString(message, (float) (x1 + 20), (float) (y1 + height / 4F), -1);
        }

    }

    public boolean shouldDelete() {
        return isFinished() && animationX >= width;
    }

    private boolean isFinished() {
        return timer.isDelayComplete(stayTime) && posY == lastY;
    }

    public double getHeight() {
        return height;
    }

    public double getAnimationState(double animation, double finalState, double speed) {
        float add = (float) (RenderUtil.delta / 1000 * speed);
        if (animation < finalState) {
            if (animation + add < finalState)
                animation += add;
            else
                animation = finalState;
        } else {
            if (animation - add > finalState)
                animation -= add;
            else
                animation = finalState;
        }
        return animation;
    }

    public void drawImage(ResourceLocation image, int x, int y, int width, int height) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public enum Type {
        SUCCESS, INFO, WARNING, ERROR
    }
}
