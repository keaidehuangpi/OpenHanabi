package cn.hanabi.modules;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.Hanabi;
import cn.hanabi.events.EventKey;
import cn.hanabi.gui.font.noway.ttfr.HFontRenderer;
import cn.hanabi.modules.modules.combat.*;
import cn.hanabi.modules.modules.ghost.*;
import cn.hanabi.modules.modules.movement.Fly.Fly;
import cn.hanabi.modules.modules.movement.*;
import cn.hanabi.modules.modules.movement.LongJump.LongJump;
import cn.hanabi.modules.modules.movement.Speed.Speed;
import cn.hanabi.modules.modules.player.*;
import cn.hanabi.modules.modules.render.*;
import cn.hanabi.modules.modules.world.*;
import cn.hanabi.gui.font.compat.WrappedVertexFontRenderer;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;



@ObfuscationClass
public class ModManager {

    @NotNull
    public static List<Mod> modules = new ArrayList<>();
    public static boolean needsort = true;
    public static ArrayList<Mod> sortedModList = new ArrayList();

    public ModManager() {
        EventManager.register(this);
    }

    @NotNull
    public static List<Mod> getModules() {
        List<Mod> temp = new ArrayList<>();
        for (Mod mod : modules) {
            if (mod.isHidden()) {
                temp.add(mod);
            }
        }
        return temp;
    }

    @NotNull
    public static List<Mod> getModules(Category category) {
        ArrayList<Mod> mods = new ArrayList<>();
        for (Mod mod : ModManager.getModList()) {
            if (mod.getCategory() == category) {
                mods.add(mod);
            }
        }
        return mods;
    }

    public static ArrayList<Mod> getEnabledModListHUD() {
        HFontRenderer font = HUD.hudMode.isCurrentMode("Classic") ? Hanabi.INSTANCE.fontManager.raleway17 : Hanabi.INSTANCE.fontManager.usans18;

        ArrayList<Mod> enabledModList = new ArrayList<>(getModules());
        enabledModList.sort((o1, o2) -> (int) (((o2.getDisplayName() != null) ? font.getStringWidth(o2.getDisplayName()) + 3 + font.getStringWidth(o2.getName()) : o2.namewidth) - (((o1.getDisplayName() != null) ? font.getStringWidth(o1.getDisplayName()) + 3 + font.getStringWidth(o1.getName()) : o1.namewidth))));
        return enabledModList;
    }

    public static Mod getModule(@NotNull String name) {
        try {
            return getModule(name, false);
        } catch (Exception e) {
            return new Mod("NMSL", Category.COMBAT) {
            };
        }
    }

    public static <T extends Mod> T getModule(Class<T> clazz) {
        return (T) modules.stream().filter(mod -> mod.getClass() == clazz).findFirst().orElse(null);
    }

    public static Mod getModule(@NotNull String name, boolean caseSensitive) {
        return modules.stream().filter(mod -> !caseSensitive && name.equalsIgnoreCase(mod.getName()) || name.equals(mod.getName())).findFirst().orElse(null);
    }

    public static List<Mod> getModList() {
        List<Mod> returnmodules = new ArrayList<>();
        for (Mod mod : modules) {
            if (mod.isHidden()) {
                returnmodules.add(mod);
            }
        }
        return returnmodules;
    }

    public static ArrayList<Mod> getEnabledModList() {
        ArrayList<Mod> enabledModList = new ArrayList<>();
        for (Mod m : getModules()) {
            if (m.isEnabled()) {
                enabledModList.add(m);
            }
        }
        return enabledModList;
    }

    public void addModules() {
        // COMBAT
        addModule(new Hitbox());
        addModule(new KeepSprint());
        addModule(new Velocity());
        addModule(new KillAura());
        addModule(new Criticals());
        addModule(new AutoSword());
        addModule(new Reach());
        addModule(new TargetStrafe());
        addModule(new AutoHeal());
        addModule(new AutoHead());
        addModule(new Regen());
        addModule(new TPAura());

        // MOVEMENT
        addModule(new Sprint());
        addModule(new Speed());
        addModule(new Fly());
        addModule(new Strafe());
        addModule(new LongJump());
        addModule(new NoSlow());
        addModule(new Step());
        addModule(new HighJump());
        addModule(new NoJumpDelay());
        addModule(new IceSpeed());
        addModule(new WaterSpeed());

        // PLAYER
        addModule(new AutoArmor());
        addModule(new InvCleaner());
        addModule(new InvMove());
        addModule(new NoFall());
        addModule(new ChatBypass());
        addModule(new LessPacket());
        addModule(new ChestStealer());
        addModule(new Nuker());
        addModule(new FastUse());
        addModule(new Blink());
        addModule(new FastPlace());
        addModule(new AutoTools());
        addModule(new AutoGG());
        addModule(new Patcher());
        addModule(new AutoPlay());
        addModule(new Mod("NoCommand", Category.PLAYER) {});
        addModule(new TeleportBedFucker());
        addModule(new Spammer());
        addModule(new AimBot());

        // RENDER
        addModule(new ClickGUIModule());
        addModule(new PaletteGui());
        addModule(new HudWindow());
        addModule(new RacistHat());
        addModule(new Nametags());
        addModule(new Fullbright());
        addModule(new ESP());
        addModule(new Projectiles());
        addModule(new BedESP());
        addModule(new ChestESP());
        addModule(new HitAnimation());
        addModule(new CaveFinder());
        addModule(new Chams());
        addModule(new OreTarget());
        addModule(new Waypoints());
        addModule(new MusicPlayer());
        addModule(new ArrowEsp());
        addModule(new NoFov());
        addModule(new NameProtect());
        addModule(new TabGUI());
        addModule(new NoHurtCam());
        addModule(new ViewClip());
        addModule(new EveryThingBlock());
        addModule(new Thermal());
        addModule(new WorldColor());
        addModule(new WorldTime());
        addModule(new WorldWeather());
        addModule(new AntiEffects());

        // WORLD
        addModule(new AntiBot());
        addModule(new Teams());
        addModule(new AntiFall());
        addModule(new AutoL());
        addModule(new HideAndSeek());
        addModule(new Eagle());
        addModule(new HUD());
        addModule(new SpeedMine());
        addModule(new UhcHelper());
        addModule(new AntiSpammer());
        addModule(new Jesus());
        addModule(new Scaffold());
        addModule(new Debug());
        addModule(new Phase());
        addModule(new Timer());
        addModule(new Spoofer());
        addModule(new MurderMystery());
        addModule(new VClip());
        addModule(new AntiAtlas());

        // GHOST
        addModule(new AutoClicker());
        addModule(new LegitVelocity());
        addModule(new SmoothAim());
        addModule(new AutoMLG());
        addModule(new DoubleClicker());
        addModule(new AutoRod());
        addModule(new Refill());
        addModule(new SafeWalk());
        addModule(new AutoPlace());
        // sort by alphabets
        modules.sort((mod, mod1) -> {
            int char0 = mod.getName().charAt(0);
            int char1 = mod1.getName().charAt(0);
            return -Integer.compare(char1, char0);
        });
    }

    public void addModule(@NotNull Mod module) {
        modules.add(module);
    }

    @EventTarget
    private void onKey(@NotNull EventKey event) {
        for (Mod module : modules)
            if (module.getKeybind() == event.getKey())
                module.setState(!module.getState());
    }

}
