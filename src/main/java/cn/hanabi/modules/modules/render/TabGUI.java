package cn.hanabi.modules.modules.render;

import cn.hanabi.Hanabi;
import cn.hanabi.events.EventKey;
import cn.hanabi.events.EventRender2D;
import cn.hanabi.gui.font.noway.ttfr.HFontRenderer;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.modules.ModManager;
import cn.hanabi.utils.RenderUtil;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.utils.fontmanager.HanabiFonts;
import cn.hanabi.gui.font.compat.WrappedVertexFontRenderer;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;


public class TabGUI extends Mod {
    private final List<Category> categoryArrayList = Arrays.asList(Category.values());
    private final TimeHelper timer = new TimeHelper();
    public int screen = -1;
    int MAIN = new Color(47, 116, 253).getRGB();
    int SECONDARY = new Color(23, 23, 23).getRGB();
    double startY = 3;
    private int currentCategoryIndex = 0;
    private int currentModIndex = 0;
    private int currentSettingIndex = 0;
    private boolean editMode = false;

    public TabGUI() {
        super("TabGUI", Category.RENDER);
    }

    public static double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @EventTarget
    public void renderTabgui(EventRender2D e) {
        SECONDARY = new Color(23, 23, 23).getRGB();
        // reset the tabgui after 5 seconds of not being used
        if (timer.hasReached(5000) && screen != -1) {
            screen = -1;
        }

        // Y轴动画
        if (screen == -1) {
            startY = RenderUtil.getAnimationStateSmooth(-20, startY, 10f / Minecraft.getDebugFPS());
        } else {
            startY = RenderUtil.getAnimationStateSmooth(70, startY, 10f / Minecraft.getDebugFPS());
        }

        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1, 1);

        int startX = 3;

        // rendering the categories first char in a row
        for (Category c : categoryArrayList) {
            RenderUtil.drawRect(startX, (float) startY, startX + 15, (float) startY + 15, SECONDARY);

            // rendering a green background on the current category
            if (getCurrentCategory() == c) {
                RenderUtil.drawRect(startX + 1, (float) startY + 1, startX + 14, (float) startY + 14, MAIN);
            }

            // center

            String needDraw = "";
            if (c == Category.COMBAT) needDraw = HanabiFonts.ICON_CLICKGUI_COMBAT;
            if (c == Category.GHOST) needDraw = HanabiFonts.ICON_CLICKGUI_GHOST;
            if (c == Category.MOVEMENT) needDraw = HanabiFonts.ICON_CLICKGUI_MOVEMENT;
            if (c == Category.PLAYER) needDraw = HanabiFonts.ICON_CLICKGUI_PLAYER;
            if (c == Category.RENDER) needDraw = HanabiFonts.ICON_CLICKGUI_RENDER;
            if (c == Category.WORLD) needDraw = HanabiFonts.ICON_CLICKGUI_WORLD;
            HFontRenderer font = Hanabi.INSTANCE.fontManager.icon20;
            font.drawString(needDraw, startX - 2 + font.getStringWidth(needDraw) / 2f, (float) startY + 3, -1);
            startX += 16;
        }

        HFontRenderer font = Hanabi.INSTANCE.fontManager.raleway16;
        // modules screen
        if (screen == 1 || screen == 2) {
            int startXMods = 3;
            int startYMods = (int) startY + 17;

            RenderUtil.drawRect(startXMods, startYMods, startXMods + getWidestMod(), startYMods + (getModsForCurrentCategory().size() * 12), SECONDARY);
            RenderUtil.drawRect(startXMods + (currentCategoryIndex * 16), (float) startY + 15, startXMods + (currentCategoryIndex * 16) + 15, startYMods, SECONDARY);

            for (Mod m : getModsForCurrentCategory()) {
                int x = startXMods + getWidestMod() - 7;
                int y = startYMods + 3 + 1;

                RenderUtil.drawRect(x, y, x + 5, y + 5, Color.BLACK.getRGB());

                if (getCurrentModule() == m) {
                    RenderUtil.drawRect(startXMods, startYMods, startXMods + font.getStringWidth(m.getName()) + 9, startYMods + 12, MAIN);
                }

                font.drawString(m.getName(), startXMods + 5, startYMods + 1 + 1, -1);


                if (m.getState()) {
                    RenderUtil.drawRect(x + 1, y + 1, x + 4, y + 4, MAIN);
                }
                startYMods += 12;
            }
        }


        // module settings screen
        if (screen == 2) {
            int startXSettings = 3 + (getWidestMod() - 7) + 9;
            int startYSettings = (int) startY + 17 + (currentModIndex * 12);
            RenderUtil.drawRect(startXSettings, startYSettings, startXSettings + getWidestSetting() + 2, startYSettings + (getSettingsForCurrentModule().size() * 12) - 2, SECONDARY);

            for (Value s : getSettingsForCurrentModule()) {

                // checking if current setting is the selected setting
                if (getCurrentSetting() == s) {
                    if (s.isValueMode) {
                        RenderUtil.drawRect(startXSettings, startYSettings, startXSettings + font.getStringWidth(s.getModeTitle() + ": ") + 3, startYSettings + 9 + 2 - 1, MAIN);
                    } else {
                        RenderUtil.drawRect(startXSettings, startYSettings, startXSettings + font.getStringWidth(s.getName() + ": ") + 3, startYSettings + 9 + 2 - 1, MAIN);
                    }
                }

                if (s.isValueDouble) {
                    font.drawString(s.getName() + ": " + (editMode && getCurrentSetting() == s ? ChatFormatting.WHITE : ChatFormatting.GRAY) + roundToPlace((double) s.getValueState(), 2), 1 + startXSettings + 3, startYSettings + 1, -1);
                } else if (s.isValueBoolean) {
                    font.drawString(s.getName() + ": " + (editMode && getCurrentSetting() == s ? ChatFormatting.WHITE : ChatFormatting.GRAY) + s.getValueState(), 1 + startXSettings + 3, startYSettings + 1, -1);
                } else {
                    font.drawString(s.getModeTitle() + ": " + (editMode && getCurrentSetting() == s ? ChatFormatting.WHITE : ChatFormatting.GRAY) + s.mode.get(s.getCurrentMode()), 1 + startXSettings + 3, startYSettings + 1, -1);
                }

                startYSettings += 12;
            }
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    @EventTarget
    public void onKeyPress(EventKey eventKeyboard) {

        if (!renderTabGUI()) {
            return;
        }

        switch (eventKeyboard.getKey()) {
            case Keyboard.KEY_RETURN: {
                timer.reset();
                enter();
                break;
            }
            case Keyboard.KEY_DOWN: {
                timer.reset();
                down();
                break;
            }
            case Keyboard.KEY_UP: {
                timer.reset();
                up();
                break;
            }
            case Keyboard.KEY_RIGHT: {
                timer.reset();
                right();
                break;
            }
            case Keyboard.KEY_LEFT: {
                timer.reset();
                left();
                break;
            }
        }
    }

    private void left() {
        if (screen == 0) {
            if (currentCategoryIndex > 0) {
                currentCategoryIndex--;
            } else if (currentCategoryIndex == 0) {
                currentCategoryIndex = categoryArrayList.size() - 1;
            }
        } else if (screen == 2) {
            currentSettingIndex = 0;
            editMode = false;
            screen = 1;
        }
    }

    private void right() {
        if (screen == 0) {
            if (currentCategoryIndex < (categoryArrayList.size() - 1)) {
                currentCategoryIndex++;
            } else {
                currentCategoryIndex = 0;
            }
        } else if (screen == 1) {
            if (!getSettingsForCurrentModule().isEmpty()) {
                screen = 2;
            }
        }
    }

    private void down() {
        if (editMode) {
            Value s = this.getCurrentSetting();
            if (s.isValueDouble) {
                Value<Double> clampedSetting = (Value<Double>) s;
                clampedSetting.setValueState(clampedSetting.getValueState() - clampedSetting.getSteps());

            } else if (s.isValueMode) {
                Value<String> modeSetting = (Value<String>) s;

                modeSetting.setCurrentMode(modeSetting.getCurrentMode() + 1);

            } else if (s.isValueBoolean) {
                ((Value<Boolean>) s).setValueState(!((Value<Boolean>) s).getValueState());
            }
        } else {
            if (screen == -1) {
                screen = 0;
            } else if (screen == 0) {
                screen = 1;
            } else if (screen == 1 && currentModIndex < (getModsForCurrentCategory().size() - 1)) {
                currentModIndex++;
            } else if (screen == 1 && currentModIndex == (getModsForCurrentCategory().size() - 1)) {
                currentModIndex = 0;
            } else if (screen == 2 && currentSettingIndex < (getSettingsForCurrentModule().size() - 1)) {
                currentSettingIndex++;
            } else if (screen == 2 && currentSettingIndex == (getSettingsForCurrentModule().size() - 1)) {
                currentSettingIndex = 0;
            }
        }
    }

    private void up() {
        if (editMode) {
            Value s = this.getCurrentSetting();
            if (s.isValueDouble) {
                Value<Double> clampedSetting = (Value<Double>) s;
                clampedSetting.setValueState(clampedSetting.getValueState() + clampedSetting.getSteps());

            } else if (s.isValueMode) {
                Value<String> modeSetting = (Value<String>) s;

                modeSetting.setCurrentMode(modeSetting.getCurrentMode() - 1);
                if (modeSetting.getCurrentMode() < 0) {
                    modeSetting.setCurrentMode(modeSetting.mode.size() - 1);
                }

            } else if (s.isValueBoolean) {
                ((Value<Boolean>) s).setValueState(!((Value<Boolean>) s).getValueState());
            }
        } else {
            if (screen == 0) {
                screen = -1;
            } else if (screen == 1 && currentModIndex == 0) {
                screen = 0;
            } else if (screen == 1 && currentModIndex > 0) {
                currentModIndex--;
            } else if (screen == 2 && currentSettingIndex > 0) {
                currentSettingIndex--;
            } else if (screen == 2 && currentSettingIndex == 0) {
                currentSettingIndex = getSettingsForCurrentModule().size() - 1;
            }
        }

    }

    private void enter() {
        if (screen == 1) {
            getCurrentModule().set(!getCurrentModule().getState());
        } else if (screen == 2) {
            editMode = !editMode;
        }
    }

    private boolean renderTabGUI() {
        return !mc.gameSettings.showDebugInfo;
    }

    private Category getCurrentCategory() {
        return categoryArrayList.get(currentCategoryIndex);
    }

    private List<Mod> getModsForCurrentCategory() {
        return ModManager.getModules(getCurrentCategory());
    }

    private Mod getCurrentModule() {
        return getModsForCurrentCategory().get(currentModIndex);
    }

    private List<Value> getSettingsForCurrentModule() {
        return Value.getValue(getCurrentModule());
    }

    private Value getCurrentSetting() {
        return getSettingsForCurrentModule().get(currentSettingIndex);
    }

    private int getWidestSetting() {
        int maxWidth = 0;
        for (Value s : getSettingsForCurrentModule()) {
            if (s.isValueDouble) {
                int width = mc.fontRendererObj.getStringWidth(s.getName() + ": " + roundToPlace((double) s.getValueState(), 2));
                if (width > maxWidth) {
                    maxWidth = width;
                }
            } else if (s.isValueMode) {
                int width = mc.fontRendererObj.getStringWidth(s.getModeTitle() + ": " + s.mode.get(s.getCurrentMode()));
                if (width > maxWidth) {
                    maxWidth = width;
                }
            } else {
                int width = mc.fontRendererObj.getStringWidth(s.getName() + ": " + s.getValueState());
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }
        }
        return maxWidth;
    }

    private int getWidestMod() {
        int width = (categoryArrayList.size() * 16);
        for (Mod m : getModsForCurrentCategory()) {
            if (mc.fontRendererObj.getStringWidth(m.getName()) + 14 > width) {
                width = mc.fontRendererObj.getStringWidth(m.getName()) + 14;
            }
        }
        return width;
    }
}
