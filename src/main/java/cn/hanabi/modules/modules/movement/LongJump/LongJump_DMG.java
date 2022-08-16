package cn.hanabi.modules.modules.movement.LongJump;

import cn.hanabi.events.EventMove;
import cn.hanabi.events.EventPreMotion;
import cn.hanabi.modules.ModManager;
import cn.hanabi.utils.MoveUtils;
import cn.hanabi.utils.PlayerUtil;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.utils.random.Random;
import cn.hanabi.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;

public class LongJump_DMG {
    final Minecraft mc = Minecraft.getMinecraft();
    private int stage;
    private double speed, verticalSpeed;

    static TimeHelper timer = new TimeHelper();
    private static final Value<Boolean> flag = new Value<>("LongJump", "Flags", false);
    private static final Value<Boolean> uhc = new Value<>("LongJump", "Extra DMG", false);
    public static final Value<Double> height = new Value<>("LongJump", "Jump Height", 1d, 0.5d, 1.4d, 0.1d);


    public void onPre(EventPreMotion e) {

    }

    public void onMove(EventMove e) {
        if (MoveUtils.isOnGround(0.01) || stage > 0) {
            switch (stage) {
                case 0:
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.004 * Math.random(), mc.thePlayer.posZ);
                    if (flag.getValue()) damage2();
                    else fallDistDamage();
                    verticalSpeed = PlayerUtil.getBaseJumpHeight() * height.getValue();
                    speed = MoveUtils.getBaseMoveSpeed(0.2877, 0.2) * 2.14;
                    break;
                case 1:
                    speed *= 0.77;
                    break;
                default:
                    speed *= 0.98;
            }
            e.setY(verticalSpeed);
            if (stage > 8) {
                verticalSpeed -= 0.032;
            } else {
                verticalSpeed *= 0.87;
            }
            stage++;

            if (MoveUtils.isOnGround(0.01) && stage > 4) {
                ModManager.getModule("LongJump").set(false);
            }
            MoveUtils.setMotion(e, Math.max(MoveUtils.getBaseMoveSpeed(0.2877, 0.1), speed));
        }
    }


    public void onEnable() {
        stage = 0;
    }

    public void onDisable() {
        stage = 0;
    }


    public static void fallDistDamage() {
        float offset;
        final double randomOffset = Math.random() * 3.000000142492354E-4;
        final double jumpHeight = 0.0625 - randomOffset;
        for (int packets = (int)(getMinFallDist() / (jumpHeight - randomOffset) + 1.0), i = 0; i < packets; ++i) {
            Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY + jumpHeight, Minecraft.getMinecraft().thePlayer.posZ, false));
            Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY + randomOffset, Minecraft.getMinecraft().thePlayer.posZ, false));
        }
        Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY, Minecraft.getMinecraft().thePlayer.posZ, true));
        /*
        for(double i = 3.0125D; i > 0.0D; i -= offset) {
            offset = Random.nextFloat(0.06241F, 0.06249F);
            Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY + (double)offset, Minecraft.getMinecraft().thePlayer.posZ, false));
            Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY, Minecraft.getMinecraft().thePlayer.posZ, false));
            if (i < (double)offset + 0.001D) {
                Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY + Math.random() / 1000.0D, Minecraft.getMinecraft().thePlayer.posZ, true));
            }
        }

         */
        timer.reset();
    }


    public static void damage2() {
        final double packets = Math.ceil(getMinFallDist() / 0.0625);
        double random = Random.nextDouble(0.00101001 , 0.00607009);
        for (int i = 0; i < packets; ++i) {
            Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY + 0.0625 + random, Minecraft.getMinecraft().thePlayer.posZ, false));
            Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY + random, Minecraft.getMinecraft().thePlayer.posZ, false));
        }
        Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer(true));
        timer.reset();
    }

    public static double getMotionY() {
        double mY = 0.41999998688697815D;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.jump)) {
            mY += (Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1;
        }
        return mY;
    }

    public static double getMinFallDist() {
        double baseFallDist = uhc.getValue() ? 4.0 : 3.0;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.jump)) {
            baseFallDist += Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1.0f;
        }
        return baseFallDist;
    }

}