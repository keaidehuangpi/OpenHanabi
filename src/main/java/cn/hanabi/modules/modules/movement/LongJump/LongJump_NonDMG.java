package cn.hanabi.modules.modules.movement.LongJump;

import cn.hanabi.events.EventMove;
import cn.hanabi.modules.ModManager;
import cn.hanabi.utils.MoveUtils;
import cn.hanabi.utils.PlayerUtil;
import net.minecraft.client.Minecraft;

public class LongJump_NonDMG {

    final Minecraft mc = Minecraft.getMinecraft();
    private int stage;
    private double speed, verticalSpeed;


    public void onMove(EventMove e) {
        if (MoveUtils.isOnGround(0.01) || stage > 0) {
            switch (stage) {
                case 0:
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.004 * Math.random(), mc.thePlayer.posZ);
                    verticalSpeed = PlayerUtil.getBaseJumpHeight();
                    speed = MoveUtils.getBaseMoveSpeed(0.2873, 0.1) * 2.149;
                    break;
                case 1:
                    speed *= 0.65;
                    break;
            }
            e.setY(verticalSpeed);
            if (stage > 8) {
                speed *= 0.98;
                verticalSpeed -= 0.035;
            } else {
                speed *= 0.99;
                verticalSpeed *= 0.65;
            }
            stage++;

            if (MoveUtils.isOnGround(0.01)  && stage > 4) {
                ModManager.getModule("LongJump").set(false);
            }

            MoveUtils.setMotion(e, Math.max(MoveUtils.getBaseMoveSpeed(0.2873, 0.1), speed));
        }
    }


    public void onEnable() {
        stage = 0;
    }

    public void onDisable() {
        stage = 0;
    }


}
