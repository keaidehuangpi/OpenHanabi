package cn.hanabi.modules.modules.movement.Speed;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.events.EventMove;
import cn.hanabi.utils.MoveUtils;
import net.minecraft.client.Minecraft;


@ObfuscationClass
public class Speed_Verus {
    Minecraft mc = Minecraft.getMinecraft();

    public void onMove(EventMove event){
        if (!mc.thePlayer.isInLava() && !mc.thePlayer.isInWater() && !mc.thePlayer.isOnLadder() && mc.thePlayer.ridingEntity == null) {
            if (MoveUtils.isMoving()) {
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                    mc.thePlayer.motionY = 0.0;
                    MoveUtils.strafe(0.61F);
                    event.y = 0.41999998688698;
                }
                MoveUtils.strafe();
            }
        }
    }
}
