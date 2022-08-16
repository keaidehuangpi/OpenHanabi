package cn.hanabi.gui.superskidder.material.clickgui.button.values;

import cn.hanabi.Hanabi;
import cn.hanabi.gui.superskidder.material.clickgui.ColorUtils;
import cn.hanabi.gui.superskidder.material.clickgui.Main;
import cn.hanabi.gui.superskidder.material.clickgui.Tab;
import cn.hanabi.gui.superskidder.material.clickgui.button.Button;
import cn.hanabi.utils.RenderUtil;
import cn.hanabi.value.Value;
import org.lwjgl.input.Mouse;

import java.awt.*;


public class BNumbers extends Button {

    public BNumbers(float x, float y, Value v, Tab moduleTab) {
        super(x, y, v, moduleTab);
    }

    @Override
    public void drawButton(float mouseX, float mouseY) {
        Hanabi.INSTANCE.fontManager.wqy16.drawString(v.getName() + ":" + v.getValue(), x, y - 2, new Color(50, 50, 50).getRGB());

        Hanabi.INSTANCE.fontManager.wqy16.drawString("-", x, y + 3, new Color(50, 50, 50).getRGB());
        Hanabi.INSTANCE.fontManager.wqy16.drawString("+", x + 65, y + 3, new Color(50, 50, 50).getRGB());

        RenderUtil.drawRect(x + 5, y + 5, x + 65, y + 6, ColorUtils.reAlpha(Main.clientColor.getRGB(), 0.6f));
        animation = animationUtils.animate(60 * (((Number) v.getValue()).floatValue() / (v.getValueMax() != null ? ((Number) v.getValueMax()).floatValue() : 0)), animation, 0.05f);
        RenderUtil.drawRect(x + 5, y + 5, x + 5 + animation, y + 6, Main.clientColor.getRGB());
        RenderUtil.circle(x + 5 + animation, y + 5.5f, 3, Main.clientColor.getRGB());

        if (Main.isHovered(x + 5, y + 4, x + 65, y + 7, mouseX, mouseY) && Mouse.isButtonDown(0)) {
            drag = true;
        } else if (!Mouse.isButtonDown(0)) {
            drag = false;
        }

        if (drag ) {
            double reach = mouseX - (x + 5);
            double percent = reach / 60f;
            double val = (((Number) v.getValueMax()).doubleValue() - ((Number) v.getValueMin()).doubleValue()) * percent;
            if (val > ((Number) v.getValueMin()).doubleValue() && val < ((Number) v.getValueMax()).doubleValue()) {
                v.setValueState((val*10)/10F);
            }

            if(val < ((Number) v.getValueMin()).doubleValue()){
                v.setValueState(((Number) v.getValueMin()));
            }else if(val > ((Number) v.getValueMax()).doubleValue()){
                v.setValueState(((Number) v.getValueMax()));
            }
        }

    }

    @Override
    public void mouseClicked(float mouseX, float mouseY) {
        super.mouseClicked(mouseX, mouseY);
    }
}
