package cn.hanabi.modules.modules.player;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.events.EventPacket;
import cn.hanabi.injection.interfaces.IC01PacketChatMessage;
import cn.hanabi.injection.interfaces.IS02PacketChat;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import org.apache.commons.lang3.StringUtils;

import java.util.Random;


@ObfuscationClass
public class ChatBypass extends Mod {

    private final Value chatbypass = new Value("ChatBypass", "Bypass", 0);

    public ChatBypass() {
        super("ChatBypass", Category.PLAYER);
        chatbypass.LoadValue(new String[]{"Russian", "Roblox", "Hypixel", "Test"});
    }

    static String unicode2String(String unicodeStr) {
        StringBuilder sb = new StringBuilder();
        String[] str = unicodeStr.toUpperCase().split("U");
        for (String s : str) {
            if (s.equals(""))
                continue;
            char c = (char) Integer.parseInt(s.trim(), 16);
            sb.append(c);
        }
        return sb.toString();
    }

    public static String toUnicode(String s) {
        String[] as = new String[s.length()];
        StringBuilder s1 = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            as[i] = Integer.toUnsignedString(s.charAt(i) & 0xffff);
            s1.append("\\u").append(as[i]);
        }
        return s1.toString();
    }

    public static String toUnicodeString(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 0 && c <= 255) {
                sb.append(c);
            } else {
                sb.append("\\u").append(Integer.toHexString(c));
            }
        }
        return sb.toString();
    }

    public static String insertPeriodically(String text, String insert, int period) {
        StringBuilder builder = new StringBuilder(
                text.length() + insert.length() * (text.length() / period) + 1);

        int index = 0;
        String prefix = "";
        while (index < text.length()) {
            // Don't put the insert in the very first iteration.
            // This is easier than appending it *after* each substring
            builder.append(prefix);

            Random random = new Random();

            //String bypass = "⛍⛗⛌⛗⛘⛉⛡⛍⛗⛉⛍⛘⛜⛍⛠⛘⛟⛏⛡⛏⛗⛏⛍⛉⛋׼";
            prefix = Character.toString(insert.charAt(random.nextInt(insert.length())));

            builder.append(text, index, Math.min(index + period, text.length()));
            index += period;
        }
        return builder.toString();
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        if (event.getPacket() instanceof S02PacketChat) {
            IS02PacketChat packet = (IS02PacketChat) event.getPacket();
            if (packet.getChatComponent().getUnformattedText().contains("℡")) {
                packet.setChatComponent(new ChatComponentText(packet.getChatComponent().getUnformattedText().replace("℡", "")));
            }
        }

        if (event.getPacket() instanceof C01PacketChatMessage) {
            final IC01PacketChatMessage packetChatMessage = (IC01PacketChatMessage) event.getPacket();
            if (packetChatMessage.getMessage().startsWith("/")) {
                return;
            }
            final StringBuilder msg = new StringBuilder();
            boolean shout = packetChatMessage.getMessage().startsWith("/shout");

            if (shout) {
                packetChatMessage.setMessage(packetChatMessage.getMessage().replaceFirst("/shout", ""));
            }

            if (chatbypass.isCurrentMode("Russian")) {
                packetChatMessage.setMessage(StringUtils.replaceChars(packetChatMessage.getMessage(), "ABESZIKMHOPCTXWVYaekmotb3hpcyx", "АВЕЅZІКМНОРСТХШѴУаекмотвзнрсух"));
            }
            if (chatbypass.isCurrentMode("Roblox")) {
                // https://instafonts.io/font/roblox-swear-bypass
                packetChatMessage.setMessage(StringUtils.replaceChars(packetChatMessage.getMessage(), "qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM", "Ɋ山乇尺ㄒㄚㄩ丨ㄖ卩卂丂ᗪ千Ꮆ卄ﾌҜㄥ乙乂匚ᐯ乃几爪1234567890Ɋ山乇尺ㄒㄚㄩ丨ㄖ卩卂丂ᗪ千Ꮆ卄ﾌҜㄥ乙乂匚ᐯ乃几爪"));
            }
            if (chatbypass.isCurrentMode("Hypixel")) {
                //  ⛍⛗⛌⛗⛘⛉⛡⛍⛗⛉⛍⛘⛜⛍⛠⛘⛟⛏⛡⛏⛗⛏⛍⛉⛋׼
                packetChatMessage.setMessage(insertPeriodically(packetChatMessage.getMessage(), "⛍⛗⛌⛗⛘⛉⛡⛍⛗⛉⛍⛘⛜⛍⛠⛘⛟⛏⛡⛏⛗⛏⛍⛉⛋׼", 1));
            }
            if (chatbypass.isCurrentMode("Test")) {
                String text = packetChatMessage.getMessage();
                StringBuilder builder = new StringBuilder(text.length() + ".".length() * (text.length() / 1) + 1);
                int index = 0;
                String prefix = "";
                while (index < text.length()) {
                    // Don't put the insert in the very first iteration.
                    // This is easier than appending it *after* each substring
                    builder.append(prefix);
                    Random random = new Random();
                    String bypass = ".,';`\"";
                    prefix = Character.toString(bypass.charAt(random.nextInt(bypass.length())));
                    builder.append(text, index, Math.min(index + 1, text.length()));
                    index += 1;
                }
                packetChatMessage.setMessage(builder.toString());
            }

            if (shout) {
                packetChatMessage.setMessage("/shout " + packetChatMessage.getMessage());
            }


            //     packetChatMessage.setMessage(msg.toString());
        }

    }
}