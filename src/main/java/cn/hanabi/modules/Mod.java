package cn.hanabi.modules;

import cn.hanabi.Client;
import cn.hanabi.Hanabi;
import cn.hanabi.gui.font.noway.ttfr.HFontRenderer;
import cn.hanabi.gui.notifications.Notification;
import cn.hanabi.modules.modules.render.HUD;
import cn.hanabi.utils.*;
import cn.hanabi.gui.font.compat.WrappedVertexFontRenderer;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public abstract class Mod {
    protected static final Minecraft mc = Minecraft.getMinecraft();

    protected static Random rand = new Random();
    static TimeHelper saveTimer = new TimeHelper();
    public String name;
    protected Category category;
    private final boolean canBeEnabled;
    private boolean hidden;
    public String displayName;
    public float posX;
    public float posY;
    public float lastY;
    public float posYRend;
    public float displaywidth;
    public float namewidth;
    public int valueSize = 0;
    public int valueSize1 = 0;
    public Button modButton;
    public Translate translate = new Translate(0.0F, 0.0F);
    public boolean keepReg = false;
    public boolean isReg = false;
    protected boolean state;
    private int keybind;
    private boolean isHidden;
    private CheckboxMenuItem checkboxMenuItem;
    private int color;


    public Mod(String name, Category Category) {
        this(name, Category, true, false, Keyboard.KEY_NONE);
    }


    public Mod(String name, Category Category, boolean canBeEnabled, boolean hidden, int keybind) {
        this.name = name;
        this.category = Category;
        this.canBeEnabled = canBeEnabled;
        this.hidden = hidden;
        this.keybind = keybind;
        this.setColor(RenderUtil.createGermanColor());
    }

    public int getColor() {return color;}

    public void setColor(int color) {this.color = color;}

    public long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }

    public int randomInt(int number) {
        return number > 0 ? rand.nextInt(number) : 0;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isCanBeEnabled() {
        return canBeEnabled;
    }

    public boolean isHidden() {
        return !hidden;
    }

    public void setHidden(boolean isHidden) {
        hidden = isHidden;
    }

    public int getKeybind() {
        return keybind;
    }

    public void setKeybind(int keybind) {
        this.keybind = keybind;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        setState(state, true);
    }

    public boolean isEnabled() {
        return state;
    }

    public void set(boolean state) {
        this.setState(state);
    }

    public void set(boolean state, boolean noti) {
        if(noti) {
            this.setState(state);
        }else{
            try {
                if (mc.thePlayer != null && saveTimer.isDelayComplete(10000)) {
                    Hanabi.INSTANCE.fileManager.save();
                    saveTimer.reset();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (state) {
                this.state = true;

                if (mc.thePlayer != null)
                    onEnable();

                if (!isReg) {
                    isReg = true;
                    EventManager.register(this);
                }
            } else {
                this.state = false;

                if (mc.thePlayer != null)
                    onDisable();


                if (!keepReg && isReg) {
                    isReg = false;
                    EventManager.unregister(this);
                }
            }
        }
    }

    public int getValueSize() {
        List<Value> i = new ArrayList<>();
        for (Value value : Value.list) {
            String valueMod = value.getValueName().split("_")[0];
            String valueName = value.getValueName().split("_")[1];
            if (!valueMod.equalsIgnoreCase(this.getName())) continue;
            i.add(value);
        }
        return i.size();
    }

    public double getAnimationState(double animation, double finalState, double speed) {
        float add = (float) (RenderUtil.delta * speed);
        if (animation < finalState) {
            if (animation + add < finalState)
                animation += add;
            else
                animation = finalState;
        } else {
            if (animation - add > finalState)
                animation -= add;
            else
                animation = finalState;
        }
        return animation;
    }

    public void onRenderArray() {
        HFontRenderer font = HUD.hudMode.isCurrentMode("Classic") ? Hanabi.INSTANCE.fontManager.raleway17 : Hanabi.INSTANCE.fontManager.usans18;

        if (namewidth == 0)
            namewidth = font.getStringWidth(name);


        if (lastY - posY > 0)
            posYRend = 14;
        if (lastY - posY < 0)
            posYRend = -14;
        if (posYRend != 0)
            posYRend = (float) RenderUtil.getAnimationStateSmooth(0, posYRend, 16.0f / Minecraft.getDebugFPS());

        float modwidth = (displayName != null) ? font.getStringWidth(displayName) + 3 + font.getStringWidth(name) : namewidth;

        if (isEnabled()) {
            posX = (float) RenderUtil.getAnimationStateSmooth(modwidth, posX, 16.0f / Minecraft.getDebugFPS());
        } else {
            posX = (float) RenderUtil.getAnimationStateSmooth(-15, posX, 16.0f / Minecraft.getDebugFPS());
        }
    }

    public void toggleModule() {
        if (this.checkboxMenuItem != null) {
            this.checkboxMenuItem.setState(!this.checkboxMenuItem.getState());
        }
        this.setState(!this.isEnabled());
    }

    public void setCheckboxMenuItem(CheckboxMenuItem checkboxMenuItem) {
        this.checkboxMenuItem = checkboxMenuItem;
    }

    public void setState(boolean state, boolean save) {
        try {
            if (save && mc.thePlayer != null && saveTimer.isDelayComplete(10000)) {
                Hanabi.INSTANCE.fileManager.save();
                saveTimer.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (state) {
            this.state = true;

            if (mc.thePlayer != null)
                onEnable();

            if (!this.getName().equals("ClickGUI") && !this.getName().equals("HUD") && !this.getName().equals("APaletteGui")) {
                if (mc.thePlayer != null) {
                    if (((HUD) ModManager.getModule("HUD")).sound.isCurrentMode("Custom1"))
                        new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.Enable, -9);
                    else if (((HUD) ModManager.getModule("HUD")).sound.isCurrentMode("Custom2"))
                        new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.Enable2, -9);
                    else
                        mc.thePlayer.playSound("random.click", 0.2F, 0.6F);

                    ClientUtil.sendClientMessage(name + " Enabled", Notification.Type.SUCCESS);
                }
            }
            if (!isReg) {
                isReg = true;
                EventManager.register(this);
            }
        } else {
            this.state = false;

            if (mc.thePlayer != null)
                onDisable();

            if (!this.getName().equals("ClickGUI") && !this.getName().equals("HUD") && !this.getName().equals("APaletteGui")) {
                if (mc.thePlayer != null)
                    if (((HUD)ModManager.getModule("HUD")).sound.isCurrentMode("Custom1"))
                        new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.Disable, -9);
                    else
                    if (((HUD)ModManager.getModule("HUD")).sound.isCurrentMode("Custom2"))
                        new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.Disable2, -9);
                    else
                        mc.thePlayer.playSound("random.click", 0.2F, 0.5F);
                ClientUtil.sendClientMessage(name + " Disabled", Notification.Type.ERROR);
            }

            if (!keepReg && isReg) {
                isReg = false;
                EventManager.unregister(this);
            }
        }
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {

        HFontRenderer font = HUD.hudMode.isCurrentMode("Classic") ? Hanabi.INSTANCE.fontManager.raleway17 : Hanabi.INSTANCE.fontManager.usans18;

        if (this.displayName == null) {
            this.displayName = displayName;
            displaywidth = font.getStringWidth(displayName);
            namewidth = font.getStringWidth(name);

            posX = -15;
            ModManager.needsort = true;
        }

        if (!this.displayName.equals(displayName)) {
            this.displayName = displayName;
            displaywidth = font.getStringWidth(displayName);
            namewidth = font.getStringWidth(name);

            posX = -15;
            ModManager.needsort = true;
        }
    }


    public boolean hasValues() {
        for (Value value : Value.list) {
            String name = value.getValueName().split("_")[0];
            if (!name.equalsIgnoreCase(this.getName())) continue;
            return true;
        }
        return false;
    }

}
