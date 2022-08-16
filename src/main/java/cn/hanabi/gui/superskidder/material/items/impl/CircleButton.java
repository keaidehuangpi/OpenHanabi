package cn.hanabi.gui.superskidder.material.items.impl;

import cn.hanabi.gui.superskidder.material.items.SButton;
import cn.hanabi.gui.superskidder.material.items.other.Shadow;
import cn.hanabi.utils.RenderUtil;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class CircleButton extends SButton {
    public CircleButton(double x, double y, double width, double height, int color) {
        super(x, y, width, height, color);
    }

    @Override
    public void draw(double mouseX, double mouseY, int mouseButton) {
//        RenderUtil.drawFilledCircle((int) x, (int) y, (float) width, color);

        for (Shadow s : ss) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtil.doGlScissor((int) x, (int) y, (int) width, (int) height);
            RenderUtil.drawFilledCircle((int) ((int) x + width / 2), (int) ((int) y + height / 2), (float) s.size, new Color(255, 255, 255, ((int) s.alpha)).getRGB());
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void onClicked(double mouseX, double mouseY, int mouseButton) {
        if (RenderUtil.isHovering((int) mouseX, (int) mouseY, (float) x, (float) y, (float) (x + width), (float) (y + height))) {
            ss.add(new Shadow(mouseX, mouseY, Math.max(width, height)));
        }
    }
}
