package cn.hanabi.modules.modules.movement.Fly;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.injection.interfaces.IKeyBinding;
import cn.hanabi.utils.PlayerUtil;
import net.minecraft.client.Minecraft;


@ObfuscationClass
public class Fly_Motion {
    Minecraft mc = Minecraft.getMinecraft();

    public void onPre() {
        this.mc.thePlayer.motionY = 0.0;

        if (PlayerUtil.MovementInput()) {
            PlayerUtil.setSpeed(Fly.timer.getValueState() * 0.5);
        } else {
            PlayerUtil.setSpeed(0);
        }

        if (((IKeyBinding) mc.gameSettings.keyBindSneak).getPress()) {
            mc.thePlayer.motionY -= 1;
        } else if (((IKeyBinding) mc.gameSettings.keyBindJump).getPress()) {
            mc.thePlayer.motionY += 1;
        }
    }

}
