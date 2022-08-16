package cn.hanabi.injection.interfaces;

import net.minecraft.network.Packet;

import java.net.InetAddress;

public interface INetworkManager {
    void sendPacketNoEvent(Packet p);

    void createNetworkManagerAndConnect(InetAddress address, int serverPort, boolean useNativeTransport);

}
