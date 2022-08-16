package cn.hanabi.utils.rotation;

import cn.hanabi.events.EventPreMotion;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class Rotation {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private float yaw, pitch;

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Rotation(EntityLivingBase entityLivingBase) {
        this.yaw = entityLivingBase.rotationYaw;
        this.pitch = entityLivingBase.rotationPitch;
    }

    public Rotation(C03PacketPlayer packetPlayer) {
        this.yaw = packetPlayer.getYaw();
        this.pitch = packetPlayer.getPitch();
    }

    public Rotation(S08PacketPlayerPosLook posLook) {
        this.yaw = posLook.getYaw();
        this.pitch = posLook.getPitch();
    }

    public void add(float yaw, float pitch) {
        setYaw(getYaw() + yaw);
        setPitch(Math.min(Math.max(getPitch() + pitch, -90), 90));
    }

    public void remove(float yaw, float pitch) {
        setYaw(getYaw() - yaw);
        setPitch(getPitch() - pitch);
    }

    public void apply() {
        mc.thePlayer.rotationYaw = getYaw();
        mc.thePlayer.rotationPitch = getPitch();
    }

    public void apply(EventPreMotion event) {
        event.setYaw(getYaw());
        event.setPitch(getPitch());
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}
