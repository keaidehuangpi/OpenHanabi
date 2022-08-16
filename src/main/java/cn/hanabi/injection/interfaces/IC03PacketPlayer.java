package cn.hanabi.injection.interfaces;

public interface IC03PacketPlayer {
    boolean isOnGround();

    void setOnGround(boolean b);

    boolean ismoving();

    void setmoving(boolean b);

    double getPosX();

    void setPosX(double x);

    double getPosY();

    void setPosY(double y);

    double getPosZ();

    void setPosZ(double z);

    float getYaw();

    void setYaw(float f);

    float getPitch();

    void setPitch(float f);

    boolean getRotate();

    void setRotate(boolean b);
}
