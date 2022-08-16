package cn.hanabi.gui.superskidder.material.clickgui.button.values;

import cn.hanabi.Hanabi;
import cn.hanabi.gui.superskidder.material.clickgui.AnimationUtils;
import cn.hanabi.gui.superskidder.material.clickgui.ColorUtils;
import cn.hanabi.gui.superskidder.material.clickgui.Main;
import cn.hanabi.gui.superskidder.material.clickgui.Tab;
import cn.hanabi.gui.superskidder.material.clickgui.button.Button;
import cn.hanabi.utils.RenderUtil;
import cn.hanabi.value.Value;

import java.awt.*;


public class BOption extends Button {
    AnimationUtils au = new AnimationUtils();

    public BOption(float x, float y, Value v, Tab moduleTab) {
        super(x, y, v, moduleTab);
    }

    @Override
    public void draw(float mouseX, float mouseY) {
        super.draw(mouseX, mouseY);
        Hanabi.INSTANCE.fontManager.wqy18.drawString(v.getName(), x + 30, y + 2, new Color(50, 50, 50).getRGB());
        if (((boolean) v.getValue())) {
            RenderUtil.drawRect(x, y + 1, x + 20, y + 9, ColorUtils.lighter(Main.clientColor, 0.3F).getRGB());
            animation = au.animate(20, animation, 0.05f);
            if (Main.isHovered(x, y + 1, x + 20, y + 9, mouseX, mouseY)) {
                RenderUtil.circle(x + animation - 3.5f, y + 5, 9, ColorUtils.reAlpha(Main.clientColor.getRGB(), 0.1f));
            }
//            if(animation != 20) {
//                RenderUtil.drawCircle(x + animation - 3.5f, y + 5, animation / 2, ColorUtils.reAlpha(Main.clientColor.getRGB(), 0.5f));
//            }
            RenderUtil.circle(x + animation - 3, y + 5, 6, new Color(240, 240, 240).getRGB());

        } else {
            RenderUtil.drawRect(x, y + 1, x + 20, y + 9, new Color(180, 180, 180).getRGB());
            animation = au.animate(0, animation, 0.05f);
            if (Main.isHovered(x, y, x + 20, y + 10, mouseX, mouseY)) {
                RenderUtil.circle(x + animation - 1.5f, y + 5, 9, new Color(0, 0, 0, 30).getRGB());
            }
            RenderUtil.circle(x + animation - 1, y + 5, 6, new Color(240, 240, 240).getRGB());
        }
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY) {
        super.mouseClicked(mouseX, mouseY);
        if (Main.isHovered(x, y, x + 20, y + 10, mouseX, mouseY)) {
            v.setValueState(!((boolean) v.getValue()));
        }
    }
}
