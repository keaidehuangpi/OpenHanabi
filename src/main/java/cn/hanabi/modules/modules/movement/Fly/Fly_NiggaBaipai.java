package cn.hanabi.modules.modules.movement.Fly;

import cn.hanabi.events.EventMove;
import cn.hanabi.events.EventPacket;
import cn.hanabi.utils.MoveUtils;
import cn.hanabi.utils.PlayerUtil;
import cn.hanabi.utils.TimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Fly_NiggaBaipai {

    Minecraft mc = Minecraft.getMinecraft();
    TimeHelper timer = new TimeHelper();

    public void onMove(EventMove e) {
        e.setX(mc.thePlayer.motionX = 0);
        e.setY(mc.thePlayer.motionY = 0);
        e.setZ(mc.thePlayer.motionZ = 0);

        if (MoveUtils.isOnGround(0.01)){
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.004 * Math.random(), mc.thePlayer.posZ);
            timer.reset(); // Don't forget reset
        }

        if (PlayerUtil.isMoving2()) {
            if (timer.isDelayComplete(1250)) // miss for .85s
            {
                double playerYaw = Math.toRadians(mc.thePlayer.rotationYaw);
                mc.thePlayer.setPosition(mc.thePlayer.posX + Fly.timer.getValueState() * 1 * -Math.sin(playerYaw), mc.thePlayer.posY - 2, mc.thePlayer.posZ + Fly.timer.getValueState() * 1 * Math.cos(playerYaw));
                timer.reset(); // Don't forget reset
            }
        } else {
            MoveUtils.setMotion(e, 0);
        }

    /*    if (((IKeyBinding) mc.gameSettings.keyBindSneak).getPress()) {
            e.setY(mc.thePlayer.motionY -= 0.25);
        } else if (((IKeyBinding) mc.gameSettings.keyBindJump).getPress()) {
            e.setY(mc.thePlayer.motionY += 0.25);
        }

        e.setOnGround(true);

     */
    }

    public void onPacket(EventPacket event) {
        if (event.getPacket() instanceof C03PacketPlayer) {
         //   mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
        }
    }


    public void onDisable() {
    //    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
    }

}
