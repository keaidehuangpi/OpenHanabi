package cn.hanabi.modules.modules.movement.Fly;

import cn.hanabi.events.*;
import cn.hanabi.gui.notifications.Notification;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.ClientUtil;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.Random;

public class Fly extends Mod {
    public static TimeHelper lagbacktimer = new TimeHelper();

    public static TimeHelper disablertimer = new TimeHelper();
    public static Value<Double> timer = new Value<>("Fly", "Motion Speed", 1d, 1d, 10d, 1d);
    Value<String> mode = new Value<>("Fly", "Mode", 0);
    Value<Boolean> lagback = new Value<>("Fly", "Lag Back Checks", true);
    Fly_Motion MotionFly = new Fly_Motion();
    Fly_Hypixel hypixelfly = new Fly_Hypixel();
    Fly_AACv5 aacv5Fly = new Fly_AACv5();
    Fly_NiggaBaipai nigga = new Fly_NiggaBaipai();

    public Fly() {
        super("Fly", Category.MOVEMENT);
        mode.addValue("Motion");
        mode.addValue("Hypixel");
        mode.addValue("AACv5");
    }

    public static double getRandomInRange(double minDouble, double maxDouble) {
        return minDouble >= maxDouble ? minDouble : new Random().nextDouble() * (maxDouble - minDouble) + minDouble;
    }

    public static void damagePlayer(int damage) {
        Minecraft mc = Minecraft.getMinecraft();
        double i1 = getRandomInRange(0.059, 0.0615);
        double i2 = getRandomInRange(0.049, 0.0625);
        double i3 = getRandomInRange(0.000000500, 0.000000700);
        final PotionEffect potioneffect = mc.thePlayer.getActivePotionEffect(Potion.jump);
        final int f = (potioneffect != null) ? (potioneffect.getAmplifier() + 1) : 0;

        //c13 exploit
/*
        if (mc.thePlayer.onGround && damage > 0) {
            for (int i = 0; i < (float) (mc.thePlayer.getMaxFallHeight() - 1 + Fly.dmgValue.getValueState() + f)
                    / 0.05510000046342611 + 1.0; ++i) {
                (mc.getNetHandler().getNetworkManager()).sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(
                        mc.thePlayer.posX, mc.thePlayer.posY + 0.05099991337, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
                (mc.getNetHandler().getNetworkManager()).sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(
                        mc.thePlayer.posX, mc.thePlayer.posY + 0.06199991337, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
                (mc.getNetHandler().getNetworkManager()).sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(
                        mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
            }
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
        }
*/
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        Packet packet = e.getPacket();

        if(mode.isCurrentMode("AACv5")){
            aacv5Fly.onPacket(e);
        }
        if(mode.isCurrentMode("Hypixel")){
            nigga.onPacket(e);
        }
    }

    @EventTarget
    public void onPre(EventPreMotion event) {
        //  this.setDisplayName(mode.getModeAt(mode.getCurrentMode()));
        if (mode.isCurrentMode("Disabler")) {
            //disabler.onMove(event);
            return;
        }


        if (mode.isCurrentMode("Motion")) {
            this.setDisplayName("FMotion");
            MotionFly.onPre();

        }


    }

    @EventTarget
    public void onPost(EventPostMotion event) {


    }

    @EventTarget
    public void onBB(BBSetEvent event) {
        if (mode.isCurrentMode("Hypixel")) {
            // GlobalHypixel.onBB(event);
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if(mode.isCurrentMode("AACv5")){
            aacv5Fly.onUpdate();
        }
    }

    @EventTarget
    public void onPullback(EventPullback e) {
        if (lagback.getValueState() && !mode.isCurrentMode("Disabler")) {
            lagbacktimer.reset();
            ClientUtil.sendClientMessage("(LagBackCheck) Fly Disabled", Notification.Type.WARNING);
            this.set(false);
        }
    }

    @EventTarget
    public void onMove(EventMove event) {
        if (mode.isCurrentMode("Hypixel")) {
            nigga.onMove(event);
            return;
        }
    }

    @Override
    public void onEnable() {
        if(mode.isCurrentMode("AACv5")){
            aacv5Fly.onEnable();
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (mode.isCurrentMode("Hypixel")) {
            nigga.onDisable();
            return;
        }
        if(mode.isCurrentMode("AACv5")){
            aacv5Fly.onDisable();
        }
        super.onDisable();
    }

    @EventTarget
    public void onStep(EventStep e) {
        if (mode.isCurrentMode("Hypixel")) {
        }
    }
}
