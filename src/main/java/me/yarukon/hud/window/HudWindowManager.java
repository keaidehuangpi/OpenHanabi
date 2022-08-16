package me.yarukon.hud.window;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.Hanabi;
import cn.hanabi.events.EventTick;
import cn.hanabi.modules.ModManager;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import me.yarukon.BlurBuffer;
import me.yarukon.hud.window.impl.WindowPlayerInventory;
import me.yarukon.hud.window.impl.WindowRadar;
import me.yarukon.hud.window.impl.WindowScoreboard;
import me.yarukon.hud.window.impl.WindowSessInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.Comparator;


@ObfuscationClass

public class HudWindowManager {

    public static Value<Boolean> blur = new Value<>("HudWindow", "Disable blur", false);

    public static final ArrayList<HudWindow> windows = new ArrayList<>();

    public Minecraft mc = Minecraft.getMinecraft();

    public HudWindow hoveredWindow;
    public HudWindow focusedWindow;
    
    public cn.hanabi.modules.modules.render.HudWindow hudWindowInstance;

    //For Session info
    public static long startTime = 0;

    public HudWindowManager() {
        register(new WindowSessInfo());
        register(new WindowPlayerInventory());
        register(new WindowScoreboard());
        register(new WindowRadar());

        hudWindowInstance = ModManager.getModule(cn.hanabi.modules.modules.render.HudWindow.class);
        EventManager.register(this);
    }

    public void register(HudWindow window) {
        windows.add(window);
    }

    public void draw() {
        if (OpenGlHelper.shadersSupported && Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer) {
            if(!blur.getValueState()) {
                if(Hanabi.INSTANCE.hasOptifine) {
                    if(Hanabi.INSTANCE.fastRenderDisabled(mc.gameSettings)) {
                        BlurBuffer.updateBlurBuffer(true);
                    }
                } else {
                    BlurBuffer.updateBlurBuffer(true);
                }
            }
        }

        for(HudWindow window : windows) {
            if(window.hide) continue;
            window.draw();
            window.postDraw();
        }
    }

    public void updateScreen() {
        for(HudWindow window : windows) {
            if(window.hide) continue;
            window.updateScreen();
        }
    }

    public void handleMouseInput(int width, int height) {
        int xx = Mouse.getEventX() * width / this.mc.displayWidth;
        int yy = height - Mouse.getEventY() * height / this.mc.displayHeight - 1;
        this.mouseMove(xx, yy);
    }

    public void mouseMove(int mouseX, int mouseY) {
        hoveredWindow = null;
        for (int i = windows.size() - 1; i >= 0; i--) {
            HudWindow window = windows.get(i);
            if (window.isOnFrame(mouseX, mouseY) && !window.hide) {
                hoveredWindow = window;
                break;
            }
        }
    }

    public void mouseClick(int mouseX, int mouseY, int mouseButton) {
        updateFocusedWindow();
        if (hoveredWindow != null)
            hoveredWindow.mouseClick(mouseX, mouseY, mouseButton);
        updateWindowOrder();
    }

    public void mouseCoordinateUpdate(int mouseX, int mouseY) {
        for(HudWindow window : windows) {
            if(window.hide) continue;
            window.mouseCoordinateUpdate(mouseX, mouseY);
        }
    }

    public void mouseRelease(int mouseX, int mouseY, int state) {
        if (hoveredWindow != null)
            hoveredWindow.mouseReleased(mouseX, mouseY, state);
    }

    public void updateFocusedWindow() {
        if (hoveredWindow == null && focusedWindow != null) {
            focusedWindow.setFocused(false);
            focusedWindow = null;
            return;
        }

        if (focusedWindow != hoveredWindow) {
            if (focusedWindow != null)
                focusedWindow.setFocused(false);
            focusedWindow = hoveredWindow;
            focusedWindow.setFocused(true);
        }
    }

    public static HudWindow getWindowByID(String windowID) {
        for(HudWindow window : windows) {
            if(window.windowID.equals(windowID)) {
                return window;
            }
        }

        return null;
    }

    public void updateWindowOrder() {
        windows.sort(Comparator.comparingLong(window -> window.lastClickTime));
    }

    @EventTarget
    public void onPre(EventTick e) {
        for (HudWindow window : HudWindowManager.windows) {
            switch (window.windowID) {
                case "SessionInfo":
                    if (hudWindowInstance.sessionInfo.getValue() && window.hide) {
                        window.show();
                    } else if (!hudWindowInstance.sessionInfo.getValue() && !window.hide) {
                        window.hide();
                    }
                    break;
                case "PlayerInventory":
                    if (hudWindowInstance.plyInventory.getValue() && window.hide) {
                        window.show();
                    } else if (!hudWindowInstance.plyInventory.getValue() && !window.hide) {
                        window.hide();
                    }
                    break;
                case "Scoreboard":
                    if (hudWindowInstance.scoreboard.getValue() && window.hide) {
                        window.show();
                    } else if (!hudWindowInstance.scoreboard.getValue() && !window.hide) {
                        window.hide();
                    }
                    break;
                case "Radar":
                    if (hudWindowInstance.radar.getValue() && window.hide) {
                        window.show();
                    } else if (!hudWindowInstance.radar.getValue() && !window.hide) {
                        window.hide();
                    }
                    break;
            }
        }
    }
}
