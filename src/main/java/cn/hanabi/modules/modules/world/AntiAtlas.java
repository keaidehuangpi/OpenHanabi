package cn.hanabi.modules.modules.world;

import cn.hanabi.events.EventChat;
import cn.hanabi.events.EventUpdate;
import cn.hanabi.events.EventWorldChange;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.modules.ModManager;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;



public class AntiAtlas extends Mod {

    public Value<Double> delay = new Value<>("AntiAtlas", "Delay", 3d, 1d, 5d, 0.5d);

    private final TimeHelper timer = new TimeHelper();
    private int index;

    public AntiAtlas() {
        super("AntiAtlas", Category.WORLD);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        index = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        index = 0;
    }

    @EventTarget
    void onLoadWorld(EventWorldChange event) {
        index = 0;
    }

    @EventTarget
    void onUpdate(EventUpdate event) {
        if (timer.isDelayComplete((delay.getValue().intValue() + 5) * 1000L)) {

            ++index;


            final ArrayList<EntityPlayer> players = new ArrayList<>(mc.theWorld.playerEntities);
            players.removeIf(player -> ModManager.getModule(AntiBot.class).isBot(player) || ModManager.getModule(Teams.class).isOnSameTeam(player));

            index = index >= players.size() ? 0 : index;

            try {
                if (players.get(index) == mc.thePlayer) return;
                mc.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage("/report " + players.get(index).getName() + " killaura"));
            } catch (Exception e) {
                index = 0;
            }

            timer.reset();
        }
    }

    @EventTarget
    void onChat(EventChat event) {
        final IChatComponent cc = event.getChatComponent();
        if (cc.getSiblings().size() == 0) {
            final ChatStyle cs = cc.getChatStyle();
            if (cs.getColor() == EnumChatFormatting.GREEN && cs.getChatClickEvent() == null && cs.getChatHoverEvent() == null) {
                event.setCancelled(true);
            }
        }
    }

}
