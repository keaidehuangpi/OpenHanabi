package cn.hanabi.modules.modules.movement.Speed;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.utils.MoveUtils;
import net.minecraft.client.Minecraft;


@ObfuscationClass
public class Speed_Mineplex {
    Minecraft mc = Minecraft.getMinecraft();

    private int boost = 0;
    private boolean jumped = false;

    public void onUpdate(){
        if(!MoveUtils.isMoving()){
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
            return;
        }

        if(jumped) {
            if(mc.thePlayer.onGround) {
                jumped=false;
                mc.thePlayer.motionX = 0.0;
                mc.thePlayer.motionZ = 0.0;
                return;
            }
            final float boostPercent;
            if(boost==0){
                boostPercent=1.75f;
            }else{
                boostPercent=1f;
            }
            MoveUtils.strafe(MoveUtils.getSpeed() * boostPercent);
            boost++;
        }else if(mc.thePlayer.onGround){
            boost=0;
            mc.thePlayer.jump();
            jumped=true;
        }

        if(mc.thePlayer.fallDistance>1.5){
            mc.thePlayer.jumpMovementFactor = 0.01f;
        }else if(mc.thePlayer.fallDistance>0){
            mc.thePlayer.jumpMovementFactor = 0.035f;
            mc.thePlayer.motionY += 0.02;
        }else{
            mc.thePlayer.jumpMovementFactor = 0.025f;
        }
    }
}
