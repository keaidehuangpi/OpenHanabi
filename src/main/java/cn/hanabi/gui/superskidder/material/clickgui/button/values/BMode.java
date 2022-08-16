package cn.hanabi.gui.superskidder.material.clickgui.button.values;

import cn.hanabi.Hanabi;
import cn.hanabi.gui.superskidder.material.clickgui.Main;
import cn.hanabi.gui.superskidder.material.clickgui.Tab;
import cn.hanabi.gui.superskidder.material.clickgui.button.Button;
import cn.hanabi.utils.RenderUtil;
import cn.hanabi.value.Value;
import org.lwjgl.input.Mouse;

import java.awt.*;


public class BMode extends Button {

    public BMode(float x, float y, Value v, Tab moduleTab) {
        super(x, y, v, moduleTab);
    }

    @Override
    public void draw(float mouseX, float mouseY) {
        RenderUtil.drawBorderedRect(x, y - 5, x + 65, y - 5 + animation, 0.5f, new Color(100, 100, 100).getRGB(), new Color(255, 255, 255).getRGB());
        RenderUtil.drawRect(x - animation / ((this.v).getModes().length * 20) * 5, y - 2 - animation / (this.v.getModes().length * 20) * 5, x - animation / ((this.v).getModes().length * 20) * 5 + Hanabi.INSTANCE.fontManager.wqy18.getStringWidth(v.getModeTitle()), y - 2 - animation / (this.v.getModes().length * 20) * 5 + 9, -1);
        Hanabi.INSTANCE.fontManager.wqy16.drawString(v.getModeTitle(), x - animation / ((this.v).getModes().length * 20) * 5, y - 2 - animation / (this.v.getModes().length * 20) * 5, new Color(150, 150, 150).getRGB());
        Hanabi.INSTANCE.fontManager.wqy18.drawString(v.getModeAt(v.getCurrentMode()), x + 5, y + 5, new Color(120, 120, 120).getRGB());
        Hanabi.INSTANCE.fontManager.wqy16.drawString("V", x + 55, y + 4, new Color(200, 200, 200).getRGB());

        float modY = y + 25;
        for (String e : v.getModes()) {
            if (e.equals(v.getModeAt(v.getCurrentMode())))
                continue;
            if (modY <= y - 5 + animation) {
                Hanabi.INSTANCE.fontManager.wqy18.drawString(e, x + 5, modY, new Color(120, 120, 120).getRGB());
//                if (Main.isHovered(x, modY - 5, x + 65, modY + 25, mouseX, mouseY) && Mouse.isButtonDown(0)) {
//                    drag = false;
//                    v.setValue(e);
//                }
            }
            modY += 20;
        }

        if (drag) {
            animation = animationUtils.animate(this.v.getModes().length * 20, animation, 0.1f);
        } else {
            animation = animationUtils.animate(20, animation, 0.1f);
        }

        super.draw(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY) {
        super.mouseClicked(mouseX, mouseY);
        if (Main.isHovered(x, y - 5, x + 65, y + 15, mouseX, mouseY)) {
            drag = !drag;
        }

        float modY = y + 25;
        for (String e : v.getModes()) {
            if (e.equals(v.getModeAt(v.getCurrentMode())))
                continue;
            if (modY <= y - 5 + animation) {
                if (Main.isHovered(x, modY, x + 65, modY + 20, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                    drag = false;
                    v.setCurrentMode(v.getModeInt(e));
                }
            }
            modY += 20;
        }
    }


}
