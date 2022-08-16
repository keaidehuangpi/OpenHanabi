package cn.hanabi.gui.superskidder.material.clickgui.Tabs;


import cn.hanabi.Hanabi;
import cn.hanabi.gui.superskidder.material.clickgui.Main;
import cn.hanabi.gui.superskidder.material.clickgui.Tab;
import cn.hanabi.gui.superskidder.material.clickgui.button.Button;
import cn.hanabi.gui.superskidder.material.clickgui.button.values.BMode;
import cn.hanabi.gui.superskidder.material.clickgui.button.values.BNumbers;
import cn.hanabi.gui.superskidder.material.clickgui.button.values.BOption;
import cn.hanabi.modules.Mod;
import cn.hanabi.value.Value;

import java.util.ArrayList;

public class ModuleTab extends Tab {
    public Mod module;
    private ArrayList<Button> btns = new ArrayList<>();

    public ModuleTab(Mod m) {
        this.module = m;
        name = m.getName();
        for (Value v : Value.getValue(module)) {
            if (v.isValueBoolean) {
                Button value = new BOption(startX, startY, v, this);
                btns.add(value);

            } else if (v.isValueByte || v.isValueLong || v.isValueDouble || v.isValueFloat) {
                Button value = new BNumbers(startX, startY, v, this);
                btns.add(value);

            } else if (v.isValueMode) {
                Button value = new BMode(startX, startY, v, this);
                btns.add(value);
            }
        }
    }


    float startX = Main.windowX + 20;
    float startY = Main.windowY + 70;

    public void render(float mouseX, float mouseY) {
        startX = Main.windowX + 20 + Main.animListX;
        startY = Main.windowY + 70;
        for (Button v : btns) {
//            if (!v.v.visible)
//                continue;
            if (!(v instanceof BMode)) {
                v.draw(mouseX, mouseY);
            }
            v.x = startX;
            v.y = startY;
            if (startX + 200 < Main.windowX + Main.windowWidth) {
                if (v instanceof BOption) {
                    startX += 40 + Hanabi.INSTANCE.fontManager.wqy18.getStringWidth(v.v.getName());
                } else {
                    startX += Math.max(80, 20 + Hanabi.INSTANCE.fontManager.wqy18.getStringWidth(v.v.getName()));
                }
            } else {
                startX = Main.windowX + 20 + Main.animListX;
                startY += 30;
            }
        }
        startX = Main.windowX + 20 + Main.animListX;
        startY = Main.windowY + 70;
        for (Button v : btns) {
//            if (!v.v.visible)
//                continue;
            if (v instanceof BMode) {
                v.draw(mouseX, mouseY);
            }
            v.x = startX;
            v.y = startY;
            if (startX + 100 + Hanabi.INSTANCE.fontManager.wqy18.getStringWidth(v.v.getName()) < Main.windowX + Main.windowWidth) {
                if (v instanceof BOption) {
                    startX += 40 + Hanabi.INSTANCE.fontManager.wqy18.getStringWidth(v.v.getName());
                } else {
                    startX += 80;
                }
            } else {
                startX = Main.windowX + 20 + Main.animListX;
                startY += 30;
            }
        }
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY) {
        super.mouseClicked(mouseX, mouseY);
        startX = Main.windowX + 20 + Main.animListX;
        startY = Main.windowY + 70;
        for (Button v : btns) {
//            if (!v.v.visible)
//                continue;
            v.mouseClick(mouseX, mouseY);
        }
    }
}
