package cn.hanabi.modules.modules.movement.Speed;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.Wrapper;
import cn.hanabi.events.EventPreMotion;
import cn.hanabi.injection.interfaces.IEntityPlayer;
import cn.hanabi.utils.MoveUtils;
import cn.hanabi.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;


@ObfuscationClass
public class Speed_AAC {
    static Value<String> mode = new Value<>("Speed", "AACMode", 0);
    public Value<Double> hytSpeed = new Value<>("Speed", "Hyt Speed", 1.0, 0.3, 8.0, 0.1);
    public Value<Double> hytMotionY = new Value<>("Speed", "Hyt MotionY", 0.42, 0.01, 2.0, 0.01);

    Minecraft mc = Minecraft.getMinecraft();

    private double[] lastPos;
    private int aac4Delay=0;
    private boolean redeskyStage=false;
    private long redeskyTimer=0L;

    public Speed_AAC() {
        mode.addValue("Hyt");
        mode.addValue("AACv4");
        mode.addValue("RedeSkyTimer");
    }

    public void onPre(EventPreMotion e) {
        if (mode.isCurrentMode("Hyt")) {
            if (mc.thePlayer.onGround && MoveUtils.isMoving()) {
                MoveUtils.strafe(hytSpeed.getValue());
                mc.thePlayer.motionY = hytMotionY.getValue();
                lastPos = null;
            } else if (lastPos == null) {
                lastPos = new double[]{mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ};
                e.setCancel(true);
            } else {
                if (lastPos.length == 3)
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(lastPos[0], lastPos[1], lastPos[2], false));
                lastPos = new double[]{};
            }
        }else if (mode.isCurrentMode("AACv4")) {
            if(MoveUtils.isMoving()){
                if(mc.thePlayer.onGround){
                    mc.thePlayer.jump();
                    aac4Delay = 0;
                }else{
                    if (aac4Delay>=3&&aac4Delay<=4) {
                        mc.thePlayer.jumpMovementFactor = 0.1F;
                    }
                    aac4Delay++;
                }
            }
        }else if (mode.isCurrentMode("RedeSkyTimer")){
            long nowTime=System.currentTimeMillis();
            if(MoveUtils.isMoving()){
                if(redeskyStage){
                    Wrapper.getTimer().timerSpeed=1.5F;
                    if((nowTime-redeskyTimer)>700L){
                        redeskyTimer=nowTime;
                        redeskyStage=!redeskyStage;
                    }
                }else{
                    Wrapper.getTimer().timerSpeed=0.8F;
                    if((nowTime-redeskyTimer)>400L){
                        redeskyTimer=nowTime;
                        redeskyStage=!redeskyStage;
                    }
                }
            }
        }
    }

    public void onEnable() {
        aac4Delay=0;
    }


    public void onDisable() {
        if (mc.thePlayer == null) return;
        ((IEntityPlayer) mc.thePlayer).setSpeedInAir(0.02F);

        Wrapper.getTimer().timerSpeed=1;
    }
}
