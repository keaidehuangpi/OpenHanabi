package cn.hanabi.modules.modules.world;

import cn.hanabi.events.EventPacket;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.Levenshtein;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.play.server.S02PacketChat;

import java.util.ArrayList;



public class AntiSpammer extends Mod {
    public static final Levenshtein lt = new Levenshtein();
    public Value<Double> history = new Value<>("AntiSpammer", "History Message", 10d, 3d, 30d, 1d);
    public Value<Double> ratio = new Value<>("AntiSpammer", "Ratio", 0.6d, 0.1d, 1d, 0.01d);
    public ArrayList<String> historyChat = new ArrayList<>();

    public AntiSpammer() {
        super("AntiSpammer", Category.WORLD);
    }

    @EventTarget
    public void onFuckingPacket(EventPacket motherfucker) {
        if (mc.thePlayer != null && mc.theWorld != null && motherfucker.getPacket() instanceof S02PacketChat) {
            S02PacketChat packet = (S02PacketChat) motherfucker.getPacket();
            String message = packet.getChatComponent().getFormattedText().replaceAll("\247.", "").replaceAll("[.*?]", "").replaceAll("<.*?>", "");
            StringBuilder result = new StringBuilder();
            int isSpace = 0;

            for (char c : message.toCharArray()) {
                if (c == ' ') {
                    isSpace++;
                }
                if (isSpace >= 2) result.append(c);
            }

            result = new StringBuilder(result.toString().replace(" ", ""));

            for (String history : historyChat) {
                double similar = lt.getSimilarityRatio(result.toString(), history);
                if (similar > ratio.getValueState()) {
                    if (this.getDisplayName() != null) {
                        this.setDisplayName((Integer.parseInt(this.getDisplayName()) + 1) + "");
                    } else {
                        this.setDisplayName(1 + "");
                    }

                    motherfucker.setCancelled(true);
                    break;
                }
            }

            historyChat.add(result.toString());
            if (historyChat.size() > history.getValueState().intValue()) historyChat.remove(0);
        }
    }
}
