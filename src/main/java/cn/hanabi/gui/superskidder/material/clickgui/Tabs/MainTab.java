package cn.hanabi.gui.superskidder.material.clickgui.Tabs;

import cn.hanabi.Hanabi;
import cn.hanabi.gui.superskidder.material.clickgui.Main;
import cn.hanabi.gui.superskidder.material.clickgui.Tab;

import java.awt.*;

public class MainTab extends Tab {
    public MainTab() {
        name = "Main Menu";
    }

    @Override
    public void render(float mouseX, float mouseY) {
        super.render(mouseX, mouseY);
        Hanabi.INSTANCE.fontManager.raleway25.drawString("Welcome!", Main.windowX + Main.animListX + 50, Main.windowY + 100, new Color(50, 50, 50).getRGB());
        float width = Hanabi.INSTANCE.fontManager.raleway25.getStringWidth("Welcome!");
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY) {
        super.mouseClicked(mouseX, mouseY);
    }
}
