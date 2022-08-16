package cn.hanabi.modules.modules.player;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.events.EventChat;
import cn.hanabi.events.EventWorldChange;
import cn.hanabi.gui.notifications.Notification;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.ClientUtil;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.event.ClickEvent;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.IChatComponent;

@ObfuscationClass

public class AutoPlay extends Mod {
    public Value<Double> delay = new Value<>("AutoPlay", "Delay", 5d, 1d, 10d, 1d);


    public AutoPlay() {
        super("AutoPlay", Category.PLAYER);
    }

    @Override
    protected void onDisable() {
    }

    @EventTarget
    public void onPacket(EventChat event) {
        for (IChatComponent cc : event.getChatComponent().getSiblings()) {
            final ClickEvent ce = cc.getChatStyle().getChatClickEvent();
            if (ce != null) {
                if ((ce.getAction() == ClickEvent.Action.RUN_COMMAND) && ce.getValue().contains("/play")) {
                    ClientUtil.sendClientMessage("Play again in " + delay.getValue() + "s", Notification.Type.SUCCESS);

             //       if (delay.getValue() > 3)
             //           new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.VICTORY, -7);

                    new Thread(() -> {
                        try {
                            Thread.sleep(delay.getValue().longValue() * 1000L);
                        } catch (final InterruptedException a) {
                            a.printStackTrace();
                        }
                        mc.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(ce.getValue()));
                    }).start();

                    event.setCancelled(true);
                }
            }
        }
    }

    @EventTarget
    public void onWorld(EventWorldChange event) {
    }
}
