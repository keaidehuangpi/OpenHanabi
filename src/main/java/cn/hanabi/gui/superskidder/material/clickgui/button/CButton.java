package cn.hanabi.gui.superskidder.material.clickgui.button;

import cn.hanabi.gui.superskidder.material.clickgui.AnimationUtils;
import cn.hanabi.gui.superskidder.material.items.impl.CircleButton;
import cn.hanabi.utils.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class CButton extends Gui {
    public boolean realized;
    private float circleR = 0;
    public String name;
    public String image;
    private float x, y;

    private float xp, yp, width, height;
    public CircleButton cb;

    public CButton(String name, String res, float xp, float yp, float w, float h) {
        this.name = name;
        this.image = res;
        this.xp = xp;
        this.yp = yp;
        this.width = w;
        this.height = h;
        cb = new CircleButton(xp, yp, w, h, new Color(0, 0, 0).getRGB());

    }


    public void onMouseClicked(float mouseX, float mouseY) {
        if (isHovered(x + 1, y + 1, x + 17, y + 17, mouseX, mouseY) && Mouse.isButtonDown(0)) {
            realized = !realized;
        }
        cb.onClicked(mouseX, mouseY, Mouse.getEventButton());
    }

    public void onRender(float x, float y, float mouseX, float mouseY) {
        this.x = x;
        this.y = y;
        onUpdate(mouseX, mouseY);
        cb.draw(mouseX, mouseY, Mouse.getEventButton());
        cb.update();
        RenderUtil.drawImage(new ResourceLocation(image), x + xp, y + yp, width, height);

    }

    public void onUpdate(float mouseX, float mouseY) {
    }

    public static boolean isHovered(float x, float y, float x2, float y2, float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }
}
