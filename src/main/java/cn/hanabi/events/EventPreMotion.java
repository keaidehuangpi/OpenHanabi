package cn.hanabi.events;

import cn.hanabi.Client;
import cn.hanabi.utils.rotation.RotationUtil;
import com.darkmagician6.eventapi.events.callables.EventCancellable;

public class EventPreMotion extends EventCancellable {
    //Created by Thread on 2020年12月11日21:22:22;
    public static float RPITCH;
    public static float RPPITCH;
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public boolean onGround;
    public boolean cancel;
    public boolean modified;

    public EventPreMotion(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
        RotationUtil.prevRotations[0] = this.yaw ;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
        Client.pitch = pitch;
        RotationUtil.prevRotations[1] = this.pitch ;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean state) {
        this.cancel = state;
    }

    public boolean isModified() {
        return modified;
    }
}