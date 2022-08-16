package cn.hanabi.gui.superskidder.material.clickgui.themes;

import cn.hanabi.Hanabi;
import cn.hanabi.gui.superskidder.material.clickgui.ColorUtils;
import cn.hanabi.gui.superskidder.material.clickgui.Main;
import cn.hanabi.gui.superskidder.material.clickgui.Tab;
import cn.hanabi.gui.superskidder.material.clickgui.Tabs.SettingsTab;
import cn.hanabi.gui.superskidder.material.clickgui.button.CButton;
import cn.hanabi.gui.superskidder.material.clickgui.renderManager.Rect;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.modules.ModManager;
import cn.hanabi.modules.modules.render.HUD;
import cn.hanabi.utils.RenderUtil;
import org.lwjgl.input.Mouse;

import java.awt.*;


public class Classic extends Main {

    @Override
    public void initGui() {
        super.initGui();
        Blist = new CButton("Modules", "Client/clickgui/modules.png", 2, 4, 12, 8);
        Btheme = new CButton("Modules", "Client/clickgui/theme.png", 3, 3, 11, 11);
        Bsettings = new CButton("Modules", "Client/clickgui/settings.png", 3, 3, 10.5f, 10.5f);
        categories.clear();
        for (Category mt : Category.values()) {
            categories.add(new cn.hanabi.gui.superskidder.material.clickgui.Category(mt, 0, false));
        }

    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }


    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }



    @Override
    public void drawTasksBar() {
        super.drawTasksBar();
        RenderUtil.drawRect(windowX + animListX, windowY + 30, windowX + windowWidth, windowY + 50, ColorUtils.reAlpha(clientColor.getRGB(), 0.6f));
        float x = 4;
        for (Tab t : tabs) {
            if (currentTab == t)
                t.render(mouseX, mouseY);

            float swidth = Hanabi.INSTANCE.fontManager.wqy16.getStringWidth(t.name) + 14;
            t.x = t.animationUtils.animate(windowX + x + animListX, t.x, drag ? 2 : 0.1F);
            new Rect(t.x, windowY + 30, swidth, 20, new Color(0, 0, 0, 0), new Runnable() {
                @Override
                public void run() {
                    if (Mouse.isButtonDown(0))
                        currentTab = t;
                }
            }).render(mouseX, mouseY);


            if (isHovered(t.x + swidth - 4, windowY + 30, t.x + swidth + 4, windowY + 50, mouseX, mouseY)) {
                Hanabi.INSTANCE.fontManager.wqy18.drawString("-", t.x + swidth - 6, windowY + 40, new Color(255, 0, 0).getRGB());
            } else {
                Hanabi.INSTANCE.fontManager.wqy18.drawString("-", t.x + swidth - 6, windowY + 40, new Color(255, 255, 255).getRGB());
            }

            if (t == currentTab) {
                Hanabi.INSTANCE.fontManager.wqy16.drawString(t.name, t.x + 2, windowY + 38, -1);
                RenderUtil.drawRect(t.x, windowY + 48, t.x + swidth, windowY + 50, clientColor.getRGB());
            } else {
                Hanabi.INSTANCE.fontManager.wqy16.drawString(t.name, t.x + 2, windowY + 38, new Color(255, 255, 255, 150).getRGB());
            }

            x += swidth;
        }

        if (Bsettings.realized) {
            Bsettings.realized = false;
            for (Tab tab: tabs){
                if (tab.name.equals("Settings")) {
                    currentTab = tab;
                    return;
                }
            }

            this.tabs.add(new SettingsTab());
        }
    }

    @Override
    public void drawList(float mouseX, float mouseY) {
        super.drawList(mouseX, mouseY);
        if (Blist.realized) {
            animListX = listAnim.animate(140, animListX, 0.2f);
        } else {
            animListX = listAnim.animate(0, animListX, 0.2f);
        }
//        GL11.glScissor((int)windowX, mc.displayHeight - (int)(windowY + windowHeight), (int)windowWidth, (int)windowHeight);
        if (animListX != 0) {
//            RenderUtil.drawRect(windowX, windowY + 30, windowX + animListX, windowY + windowHeight, new Color(255, 255, 255).getRGB());
            RenderUtil.drawGradientSideways(windowX + animListX, windowY + 30, windowX + animListX + 3, windowY + windowHeight, new Color(50, 50, 50, 100).getRGB(), new Color(255, 255, 255, 0).getRGB());
            float dWheel = Mouse.getDWheel();

//            modsY = windowY + 35;

            if (dWheel > 0 && listRoll2 < 0) {
                listRoll2 += 32;
            } else if (dWheel < 0) {
                listRoll2 -= 32;
            }

            listRoll = rollAnim.animate(listRoll2, listRoll, 0.05f);

            float modsY = windowY + 35 + listRoll;


            for (cn.hanabi.gui.superskidder.material.clickgui.Category mt : categories) {
                if (mt.show || mt.needRemove) {
//                    new Rect(windowX, modsY, animListX, 20, new Color(255, 255, 255), new Runnable() {
//                        @Override
//                        public void run() {
//                        }
//                    }).render(mouseX, mouseY);
                    Hanabi.INSTANCE.fontManager.wqy18.drawString(mt.moduleType.name(), windowX + animListX - 130, modsY + 5, new Color(0, 0, 0).getRGB());
                    modsY += 25;
                    mt.modsY2 = 0;
                    for (Mod m : ModManager.getModules(mt.moduleType)) {
                        new Rect(windowX, modsY + mt.modsY2, animListX, 15, new Color(255, 255, 255), new Runnable() {
                            @Override
                            public void run() {
                            }
                        }).render(mouseX, mouseY);
                        if (modsY + 5 + mt.modsY2 < modsY + mt.modsY3 + 25)
                            Hanabi.INSTANCE.fontManager.wqy18.drawString(m.getName(), windowX + animListX - 120, modsY + 5 + mt.modsY2, m.isEnabled()? HUD.design.getColor():new Color(50, 50, 50).getRGB());
                        mt.modsY2 += 20;
                    }

                    if (mt.needRemove) {
                        mt.modsY3 = mt.rollAnim2.animate(-25, mt.modsY3, 0.1f);
                        if (mt.modsY3 == -25) {
                            mt.needRemove = false;
                            mt.show = false;
                        }
                    } else {
                        mt.modsY3 = mt.rollAnim2.animate(mt.modsY2, mt.modsY3, 0.1f);
                    }

                    modsY += mt.modsY3;
                } else {
                    new Rect(windowX, modsY, animListX, 20, new Color(255, 255, 255), new Runnable() {
                        @Override
                        public void run() {
                        }
                    }).render(mouseX, mouseY);
                    Hanabi.INSTANCE.fontManager.wqy18.drawString(mt.moduleType.name(), windowX + animListX - 130, modsY + 5, new Color(0, 0, 0).getRGB());

                }

                modsY += 25;
            }
        }

    }

    @Override
    public void drawWindow(float mouseX, float mouseY) {
        RenderUtil.drawRoundedRect(windowX, windowY, windowX + windowWidth, windowY + windowHeight, 2, new Color(255, 255, 255).getRGB());

        new Rect(windowX, windowY, windowWidth, 30, clientColor, new Runnable() {
            @Override
            public void run() {
                if (!Mouse.isButtonDown(0))
                    return;
                drag = true;
                if (mouseDX == 0) {
                    mouseDX = mouseX - windowX;
                }
                if (mouseDY == 0) {
                    mouseDY = mouseY - windowY;
                }
//                mouseDX = 0;
//                mouseDY = 0;
            }
        }).render(mouseX, mouseY);
    }
}
