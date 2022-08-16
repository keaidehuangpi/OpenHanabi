package cn.hanabi.injection.interfaces;

public interface IS32PacketConfirmTransaction {

    void setwindowId(int b);

    int getwindowID();

    short getUid();

    void setUid(short b);

    boolean getAccepted();

    void setAccepted(boolean b);

}