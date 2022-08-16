package cn.hanabi.modules.modules.movement.Fly;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.events.EventMove;
import cn.hanabi.events.EventPreMotion;
import cn.hanabi.utils.TimeHelper;
import net.minecraft.client.Minecraft;


@ObfuscationClass
public class Fly_Hypixel {
    Minecraft mc = Minecraft.getMinecraft();

    TimeHelper timer = new TimeHelper();

    public void onPre(EventPreMotion e) {
        if (timer.isDelayComplete(850)) // miss for .85s
        {
            HClip(0.5); // HClip (Inspired by Rise Client)
            timer.reset(); // Don't forget reset
        }
    }

    public void onMove(EventMove event) {
        event.setX(0);
        event.setY(0);
        event.setZ(0);

    }

    private void HClip(double horizontal) {
        double playerYaw = Math.toRadians(mc.thePlayer.rotationYaw);
        mc.thePlayer.setPosition(mc.thePlayer.posX + horizontal * -Math.sin(playerYaw), mc.thePlayer.posY, mc.thePlayer.posZ + horizontal * Math.cos(playerYaw));
    }


}
