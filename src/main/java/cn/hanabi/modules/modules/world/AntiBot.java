package cn.hanabi.modules.modules.world;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.events.EventPacket;
import cn.hanabi.events.EventUpdate;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.modules.ModManager;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@ObfuscationClass

public class AntiBot extends Mod {
    private static final Value mode = new Value("AntiBot", "Mode", 0);
    private static final List<Entity> invalid = new CopyOnWriteArrayList<>();
    private static final List<Entity> whitelist = new CopyOnWriteArrayList<>();

    public Value<Boolean> remove = new Value<>("AntiBot", "Removed", true);
    public int count = 0;

    public AntiBot() {
        super("AntiBot", Category.COMBAT);
        mode.LoadValue(new String[]{"Hypixel", "Mineplex", "Advanced", "MineLand", "HuaYuTing"});
        setState(true);
    }

    public static boolean isBot(Entity e) {
        if (!(e instanceof EntityPlayer) || !ModManager.getModule("AntiBot").isEnabled())
            return false;
        EntityPlayer player = (EntityPlayer) e;

        if (mode.isCurrentMode("Hypixel")) {
            return (!inTab(player) && !whitelist.contains(player));
        }

        return mode.isCurrentMode("Mineplex") && !Float.isNaN(player.getHealth());
    }


    private static boolean inTab(EntityLivingBase entity) {
        for (NetworkPlayerInfo info : mc.getNetHandler().getPlayerInfoMap())
            if (info != null && info.getGameProfile() != null && info.getGameProfile().getName().contains(entity.getName()))
                return true;
        return false;
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventTarget
    public void onReceivePacket(EventPacket event) {
    }

    @EventTarget
    public void onUpdate(EventUpdate e) {
        this.setDisplayName(mode.getModeAt(mode.getCurrentMode()));

        if (mc.isSingleplayer()) return;

        if (mode.isCurrentMode("MineLand")) {
            if (!mc.theWorld.getLoadedEntityList().isEmpty()) {
                for (Entity ent : mc.theWorld.getLoadedEntityList()) {
                    if (ent instanceof EntityPlayer) {
                        if (!invalid.contains(ent) && mc.thePlayer.getDistanceToEntity(ent) > 20) {
                            invalid.add(ent);
                        }
                        if (ent != mc.thePlayer && !invalid.contains(ent) && mc.thePlayer.getDistanceToEntity(ent) < 10) {
                            mc.theWorld.removeEntity(ent);
                        }
                    }
                }
            }
        }

        if (mc.thePlayer.ticksExisted < 5)
            whitelist.clear();


        if (mc.thePlayer.ticksExisted % 60 == 0)
            whitelist.clear();


        if (mode.isCurrentMode("Hypixel")) {
            if (!mc.theWorld.getLoadedEntityList().isEmpty()) {
                for (Entity ent : mc.theWorld.getLoadedEntityList()) {
                    if (ent instanceof EntityPlayer) {
                        if (!whitelist.contains(ent)) {
                            String formatted = ent.getDisplayName().getFormattedText();
                            // PlayerUtil.debug(formatted);
                            //name check
                            //npc check
                            if (formatted.startsWith("\247r\2478[NPC]"))
                                return;

                            //distance check
                            if (!ent.isInvisible())
                                whitelist.add(ent);

                            // hurttime check
                            if (ent.hurtResistantTime == 8)
                                whitelist.add(ent);
                        }

                    }
                }
            }
        }


    }
}

