package cn.hanabi.events;

import cn.hanabi.injection.interfaces.IEntityPlayerSP;
import com.darkmagician6.eventapi.events.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;

public class EventMove implements Event {
    public double x;
    public double y;
    public double z;
    private boolean onGround;

    public EventMove(double a, double b, double c) {
        this.x = a;
        this.y = b;
        this.z = c;
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }


    public void setSpeed(double speed) {
        float yaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
        double forward = Minecraft.getMinecraft().thePlayer.movementInput.moveForward;
        double strafe = Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe;
        if (forward == 0 && strafe == 0) {
            setX(0);
            setZ(0);
        } else {
            if (forward != 0) {
                if (strafe > 0) {
                    yaw += (forward > 0 ? -45 : 45);
                } else if (strafe < 0) {
                    yaw += (forward > 0 ? 45 : -45);
                }
                strafe = 0;
                if (forward > 0) {
                    forward = 1;
                } else {
                    forward = -1;
                }
            }
            double cos = Math.cos(Math.toRadians(yaw + 90));
            double sin = Math.sin(Math.toRadians(yaw + 90));
            setX(forward * speed * cos + strafe * speed * sin);
            setZ(forward * speed * sin - strafe * speed * cos);
        }
    }

    public void setSpeed(double speed, float yaw, double strafe, double forward) {
        if (forward == 0 && strafe == 0) {
            setX(0);
            setZ(0);
        } else {
            if (forward != 0) {
                if (strafe > 0) {
                    yaw += (forward > 0 ? -45 : 45);
                } else if (strafe < 0) {
                    yaw += (forward > 0 ? 45 : -45);
                }
                strafe = 0;
                if (forward > 0) {
                    forward = 1;
                } else {
                    forward = -1;
                }
            }
            double cos = Math.cos(Math.toRadians(yaw + 90));
            double sin = Math.sin(Math.toRadians(yaw + 90));
            setX(forward * speed * cos + strafe * speed * sin);
            setZ(forward * speed * sin - strafe * speed * cos);
        }
    }

    public double getMovementSpeed() {
        double baseSpeed = 0.2873D;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }

    public void setMovementSpeed(double movementSpeed) {
        setX(-(Math.sin(((IEntityPlayerSP) Minecraft.getMinecraft().thePlayer).getDirection())
                * Math.max(movementSpeed, getMovementSpeed())));
        setZ(Math.cos(((IEntityPlayerSP) Minecraft.getMinecraft().thePlayer).getDirection())
                * Math.max(movementSpeed, getMovementSpeed()));
    }

    public double getMotionY(double mY) {
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.jump)) {
            mY += (Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1;
        }
        return mY;
    }

    public double getLegitMotion() {
        return 0.41999998688697815D;
    }

}
