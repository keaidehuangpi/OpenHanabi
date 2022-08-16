package cn.hanabi.utils;

import cn.hanabi.Hanabi;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IChatComponent;

public class ChatUtils {
    public static final String PRIMARY_COLOR = "\2477";
    public static final String SECONDARY_COLOR = "\2471";
    private static final String PREFIX = PRIMARY_COLOR + "[" + SECONDARY_COLOR + Hanabi.CLIENT_NAME + PRIMARY_COLOR + "] ";

    public static void send(final String s) {
        JsonObject object = new JsonObject();
        object.addProperty("text", s);
        Minecraft.getMinecraft().thePlayer.addChatMessage(IChatComponent.Serializer.jsonToComponent(object.toString()));
    }

    public static void success(String s) {
        info(s);
    }

    public static void info(String s) {
        send(PREFIX + s);
    }

}
