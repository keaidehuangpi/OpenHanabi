package cn.hanabi.utils.auth.client;

import cn.hanabi.utils.auth.websocket.WebSocket;

import java.net.URI;
import java.net.URISyntaxException;

public class AuthClient {
    public static void Login (String Username,String Password,String HWID) {
        String  PacketJson = String.format("{\"type\":\"LoginMessage\",\"data\":\"{\\\"Version\\\":\\\"1.00\\\",\\\"UserName\\\":\\\"%s\\\",\\\"Password\\\":\\\"%s\\\",\\\"HWID\\\":\\\"%s\\\"}\"}",Username,Password,HWID);
        try {
            WebSocket websocket = new WebSocket(new URI("wss://hanabi.rbq.wtf/auth"));
            websocket.connect();;
            websocket.setSendData(PacketJson);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
