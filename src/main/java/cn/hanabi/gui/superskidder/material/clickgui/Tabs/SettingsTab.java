package cn.hanabi.gui.superskidder.material.clickgui.Tabs;



import cn.hanabi.Hanabi;
import cn.hanabi.gui.superskidder.material.clickgui.Main;
import cn.hanabi.gui.superskidder.material.clickgui.Tab;
import cn.hanabi.gui.superskidder.material.clickgui.button.Button;
import cn.hanabi.gui.superskidder.material.clickgui.button.values.BMode;
import cn.hanabi.gui.superskidder.material.clickgui.button.values.BNumbers;
import cn.hanabi.gui.superskidder.material.clickgui.button.values.BOption;
import cn.hanabi.modules.ModManager;
import cn.hanabi.modules.modules.render.HUD;
import cn.hanabi.value.Value;

import java.util.ArrayList;

public class SettingsTab extends Tab {
    private ArrayList<Button> btns = new ArrayList<>();

    public SettingsTab() {
        name = "Settings";
        for (Value v : Value.getValue(ModManager.getModule(HUD.class))) {
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
            v.x = startX;
            v.y = startY;
            v.draw(mouseX, mouseY);
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
            v.mouseClicked(mouseX, mouseY);
        }
    }
}
