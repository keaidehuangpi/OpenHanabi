package cn.hanabi.modules.modules.movement.Speed;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.Wrapper;
import cn.hanabi.events.EventPreMotion;
import cn.hanabi.modules.ModManager;
import cn.hanabi.modules.modules.combat.KillAura;
import cn.hanabi.modules.modules.combat.TargetStrafe;
import cn.hanabi.utils.PlayerUtil;
import net.minecraft.client.Minecraft;


@ObfuscationClass
public class Speed_GudHop {
    Minecraft mc = Minecraft.getMinecraft();

    public void onPre(EventPreMotion e) {
        final KillAura killAura = ModManager.getModule(KillAura.class);
        final TargetStrafe targetStrafe = ModManager.getModule(TargetStrafe.class);

        if (mc.thePlayer.onGround && PlayerUtil.MovementInput() && !mc.thePlayer.isInWater()) {
            Wrapper.getTimer().timerSpeed = 1.0F;
            mc.thePlayer.jump();
        } else if (PlayerUtil.MovementInput() && !mc.thePlayer.isInWater()) {

            if (targetStrafe.isStrafing(null, killAura.target, 1))
                PlayerUtil.setSpeed(1);

        }

        if (!PlayerUtil.MovementInput()) {
            mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0D;
        }
    }
}
