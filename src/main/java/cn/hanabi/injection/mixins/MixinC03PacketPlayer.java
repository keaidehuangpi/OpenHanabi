package cn.hanabi.injection.mixins;

import cn.hanabi.injection.interfaces.IC03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(C03PacketPlayer.class)
public class MixinC03PacketPlayer implements IC03PacketPlayer {

    @Shadow
    protected boolean onGround;

    @Shadow
    protected double x;
    @Shadow
    protected double y;
    @Shadow
    protected double z;
    @Shadow
    protected float yaw;
    @Shadow
    protected float pitch;
    @Shadow
    protected boolean rotating;
    @Shadow
    protected boolean moving;

    @Override
    public boolean ismoving() {
        return moving;
    }

    @Override
    public void setmoving(boolean b) {
        moving = b;
    }


    @Override
    public boolean isOnGround() {
        return onGround;
    }

    @Override
    public void setOnGround(boolean b) {
        onGround = b;
    }

    @Override
    public double getPosX() {
        return x;
    }

    @Override
    public void setPosX(double x) {
        this.x = x;
    }

    @Override
    public double getPosY() {
        return y;
    }

    @Override
    public void setPosY(double y) {
        this.y = y;
    }

    @Override
    public double getPosZ() {
        return z;
    }

    @Override
    public void setPosZ(double z) {
        this.z = z;
    }

    @Override
    public float getYaw() {
        // TODO Auto-generated method stub
        return yaw;
    }

    @Override
    public void setYaw(float f) {
        yaw = f;
    }

    @Override
    public float getPitch() {
        // TODO Auto-generated method stub
        return pitch;
    }

    @Override
    public void setPitch(float f) {
        pitch = f;

    }

    @Override
    public boolean getRotate() {
        // TODO Auto-generated method stub
        return rotating;
    }

    @Override
    public void setRotate(boolean b) {
        // TODO Auto-generated method stub
        rotating = b;
    }

}
