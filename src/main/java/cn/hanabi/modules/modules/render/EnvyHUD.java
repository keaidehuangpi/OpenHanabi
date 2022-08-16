package cn.hanabi.modules.modules.render;

import cn.hanabi.Hanabi;
import cn.hanabi.events.EventKey;
import cn.hanabi.events.EventRender2D;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.modules.ModManager;
import cn.hanabi.utils.ClientUtil;
import cn.hanabi.utils.Colors;
import cn.hanabi.utils.RenderUtil;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.utils.fontmanager.HanabiFonts;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class EnvyHUD extends Mod {
    public static final int MAIN = new Color(33, 170, 47).getRGB();
    public static final int SECONDARY = new Color(23, 23, 23).getRGB();

    private final List<Category> categoryArrayList = Arrays.asList(Category.values());
    private final TimeHelper timer = new TimeHelper();
    public Gui guiInstance = new Gui();
    private int currentCategoryIndex = 0;
    private int currentModIndex = 0;
    private int currentSettingIndex = 0;
    private int screen = -1;
    private boolean editMode = false;

    public EnvyHUD() {
        super("EHUD", Category.RENDER);
    }

    public static double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Draw a 1 pixel wide horizontal line. Args: x1, x2, y, color
     */
    public static void drawHorizontalLine(int startX, int endX, int y, int color) {
        if (endX < startX) {
            int i = startX;
            startX = endX;
            endX = i;
        }

        Gui.drawRect(startX, y, endX + 1, y + 1, color);
    }

    /**
     * Draw a 1 pixel wide vertical line. Args : x, y1, y2, color
     */
    public static void drawVerticalLine(int x, int startY, int endY, int color) {
        if (endY < startY) {
            int i = startY;
            startY = endY;
            endY = i;
        }

        Gui.drawRect(x, startY + 1, x + 1, endY, color);
    }

    @EventTarget
    public void onRender(EventRender2D e) {
        if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            return;
        }

        GlStateManager.pushMatrix();

        int yStart = 1;

        ScaledResolution sr = new ScaledResolution(mc);

        List<Mod> mods = ModManager.getEnabledModList();
        mods.sort((o1, o2) -> mc.fontRendererObj.getStringWidth(o2.getDisplayName() == null ? o2.getName() : o2.getName() + "," + o2.getDisplayName()) - mc.fontRendererObj.getStringWidth(o1.getDisplayName() == null ? o1.getName() : o1.getName() + "," + o1.getDisplayName()));

        for (Mod module : mods) {
            int startX = sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(module.getDisplayName() == null ? module.getName() : module.getName() + "," + module.getDisplayName()) - 5;

            Gui.drawRect(startX, yStart - 1, sr.getScaledWidth(), yStart + 10, SECONDARY);
            Gui.drawRect(sr.getScaledWidth() - 1, yStart - 1, sr.getScaledWidth(), yStart + 10, MAIN);

            drawVerticalLine(startX - 1, yStart - 2, yStart + 10, MAIN);
            drawHorizontalLine(startX - 1, sr.getScaledWidth() + 1, yStart + 10, MAIN);

            mc.fontRendererObj.drawStringWithShadow(module.getName(), startX + 3, yStart, MAIN);
            mc.fontRendererObj.drawStringWithShadow(module.getDisplayName(), startX + 3 + mc.fontRendererObj.getStringWidth(module.getName() + ","), yStart, Color.WHITE.darker().darker().getRGB());

            yStart += 11;
        }

        GlStateManager.popMatrix();


        RenderUtil.drawRect((sr.getScaledWidth() / 2) - 91 + mc.thePlayer.inventory.currentItem * 20 + 1, sr.getScaledHeight() - 20,
                (sr.getScaledWidth() / 2) + 90 - 20 * (8 - mc.thePlayer.inventory.currentItem), sr.getScaledHeight(),
                ClientUtil.reAlpha(Colors.WHITE.c, 0.5f));

        RenderHelper.enableGUIStandardItemLighting();
        for (int j = 0; j < 9; ++j) {
            if (j != mc.thePlayer.inventory.currentItem)
                RenderUtil.drawRect((sr.getScaledWidth() / 2) - 91 + j * 20 + 1, sr.getScaledHeight() - 20,
                        (sr.getScaledWidth() / 2) + 90 - 20 * (8 - j), sr.getScaledHeight(),
                        ClientUtil.reAlpha(Colors.BLACK.c, 0.5f));
            int k = sr.getScaledWidth() / 2 - 90 + j * 20 + 2;
            int l = sr.getScaledHeight() - 16 - 2;
            ((HUD) ModManager.getModule("HUD")).customRenderHotbarItem(j, k, l, e.partialTicks, mc.thePlayer);
        }

        GlStateManager.disableBlend();
        GlStateManager.color(1, 1, 1);

        RenderHelper.disableStandardItemLighting();

        ClientUtil.INSTANCE.drawNotifications();
    }

    @EventTarget
    public void renderTabgui(EventRender2D e) {
        // reset the tabgui after 5 seconds of not being used
        if (timer.hasReached(5000) && screen == 0) {
            screen = -1;
        }

        // rendering the logo
        if (screen == -1) {
            Hanabi.INSTANCE.fontManager.icon130.drawStringWithShadow(HanabiFonts.ICON_HANABI_LOGO, 2, 2,
                    MAIN);
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1, 1);

        int startX = 3;
        int startY = 3;

        // rendering the categories first char in a row
        for (Category c : categoryArrayList) {
            Gui.drawRect(startX, startY, startX + 15, startY + 15, SECONDARY);

            // rendering a green background on the current category
            if (getCurrentCategory() == c) {
                Gui.drawRect(startX + 1, startY + 1, startX + 14, startY + 14, MAIN);
            }

            // center
            mc.fontRendererObj.drawStringWithShadow(c.name().substring(0, 1), startX + 8 - mc.fontRendererObj.getStringWidth(c.name().substring(0, 1)) / 2, startY + 4, -1);
            startX += 16;
        }

        // modules screen
        if (screen == 1 || screen == 2) {
            int startXMods = 3;
            int startYMods = 3 + 17;

            Gui.drawRect(startXMods, startYMods, startXMods + getWidestMod(), startYMods + (getModsForCurrentCategory().size() * 12), SECONDARY);
            Gui.drawRect(startXMods + (currentCategoryIndex * 16), startY + 15, startXMods + (currentCategoryIndex * 16) + 15, startYMods, SECONDARY);

            for (Mod m : getModsForCurrentCategory()) {

                if (getCurrentModule() == m) {
                    Gui.drawRect(startXMods, startYMods, startXMods + mc.fontRendererObj.getStringWidth(m.getName()) + 4, startYMods + 12, MAIN);
                }

                int x = startXMods + getWidestMod() - 7;
                int y = startYMods + 3 + 1;

                Gui.drawRect(x, y, x + 5, y + 5, -16777216);

                if (m.getState()) {
                    Gui.drawRect(x + 1, y + 1, x + 4, y + 4, MAIN);
                }

                mc.fontRendererObj.drawStringWithShadow(m.getName(), startXMods + 2, startYMods + 1 + 1, -1);
                startYMods += 12;
            }
        }


        // module settings screen
        if (screen == 2) {
            int startXSettings = 3 + (getWidestMod() - 7) + 9;
            int startYSettings = 3 + 17 + (currentModIndex * 12);
            Gui.drawRect(startXSettings, startYSettings, startXSettings + getWidestSetting() + 2, startYSettings + (getSettingsForCurrentModule().size() * 12) - 2, SECONDARY);

            for (Value s : getSettingsForCurrentModule()) {

                // checking if current setting is the selected setting
                if (getCurrentSetting() == s) {
                    Gui.drawRect(startXSettings, startYSettings, startXSettings + mc.fontRendererObj.getStringWidth(s.getName() + ": "), startYSettings + 9 + 2 - 1, MAIN);
                }

                if (s.isValueDouble) {
                    mc.fontRendererObj.drawStringWithShadow(s.getName() + ": " + (editMode && getCurrentSetting() == s ? ChatFormatting.WHITE : ChatFormatting.GRAY) + roundToPlace((double) s.getValueState(), 2), 1 + startXSettings, startYSettings + 1, -1);
                } else if (s.isValueBoolean) {
                    mc.fontRendererObj.drawStringWithShadow(s.getName() + ": " + (editMode && getCurrentSetting() == s ? ChatFormatting.WHITE : ChatFormatting.GRAY) + s.getValueState(), 1 + startXSettings, startYSettings + 1, -1);
                } else {
                    mc.fontRendererObj.drawStringWithShadow(s.getModeTitle() + ": " + (editMode && getCurrentSetting() == s ? ChatFormatting.WHITE : ChatFormatting.GRAY) + s.mode.get(s.getCurrentMode()), 1 + startXSettings, startYSettings + 1, -1);
                }

                startYSettings += 12;
            }
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    @EventTarget
    public void armor(EventRender2D e) {

    }

    @EventTarget
    public void potion(EventRender2D e) {
        ScaledResolution sr = new ScaledResolution(mc);
        GlStateManager.pushMatrix();

        int size = 16;

        int yOffset = 10;

        if (Minecraft.getMinecraft().ingameGUI.getChatGUI().getChatOpen())
            yOffset = -3;

        float x = (sr.getScaledWidth() - size * 2) + 8;
        float y = (sr.getScaledHeight() - size * 2) + yOffset;

        Collection<PotionEffect> effects = Minecraft.getMinecraft().thePlayer.getActivePotionEffects();

        int i = 0;
        if (!effects.isEmpty()) {

            for (PotionEffect potionEffect : effects) {

                Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
                int potionDuration = potionEffect.getDuration();
                String potionDurationFormatted = Potion.getDurationString(potionEffect);

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));

                if (potion.hasStatusIcon()) {
                    int var9 = potion.getStatusIconIndex();
                    guiInstance.drawTexturedModalRect((int) x, (int) y - (18 * i), var9 % 8 * 18, 198 + var9 / 8 * 18, 18, 18);
                    mc.fontRendererObj.drawStringWithShadow("" + (potionDuration <= 300 ? ChatFormatting.RED : ChatFormatting.WHITE) + Potion.getDurationString(potionEffect), x - mc.fontRendererObj.getStringWidth(potionDurationFormatted) - 5, y - (18 * i) + 6, -1);
                    i++;
                }

            }
        }

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
