package cn.hanabi.modules.modules.player;

import cn.hanabi.events.EventPacket;
import cn.hanabi.events.EventUpdate;
import cn.hanabi.gui.notifications.Notification;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.modules.ModManager;
import cn.hanabi.utils.ClientUtil;
import cn.hanabi.utils.SoundFxPlayer;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.ScreenShotHelper;

public class AutoGG extends Mod {
    public Value<Boolean> sceenshot = new Value<>("AutoGG", "SceenShot", true);
    public Value<Double> delay = new Value<>("AutoGG", "Speak Delay", 100d, 100d, 3000d, 100d);

    public boolean needSpeak = false;
    public boolean speaked = false;
    public boolean notiSent = false;
    public TimeHelper timer = new TimeHelper();
    public Notification noti = new Notification("", Notification.Type.INFO);
    public String playCommand = "";
    public String lastTitle = "";

    public AutoGG() {
        super("AutoGG", Category.PLAYER);
        // TODO 自动生成的构造函数存根
    }

    @EventTarget
    public void onUpdate(EventUpdate e) {
        if (mc.thePlayer != null) {
            AutoPlay autoPlay = (AutoPlay) ModManager.getModule("AutoPlay");
            boolean hypMode = true;
            if (needSpeak) {
                if (!speaked && timer.isDelayComplete(delay.getValueState())) {
                    speaked = true;
                    mc.thePlayer.sendChatMessage("/ac GG");
                    if (sceenshot.getValueState()) {
                        mc.ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(mc.mcDataDir, mc.displayWidth, mc.displayHeight, mc.getFramebuffer()));
                    }
                    if (ModManager.getModule("AutoPlay").getState()) {
                        ClientUtil.notifications.add(noti);
                        new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.Notification, -7);
                    }
                }

                if (speaked) {
                    if (timer.isDelayComplete(autoPlay.delay.getValueState() + delay.getValueState())) {
                        speaked = false;
                        needSpeak = false;
                    }
                }
            }
        }
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if (e.getPacket() instanceof S45PacketTitle) {
            S45PacketTitle packet = (S45PacketTitle) e.getPacket();
            String title = packet.getMessage().getFormattedText();
            if ((title.startsWith("\2476\247l") && title.endsWith("\247r")) || (title.startsWith("\247c\247lYOU") && title.endsWith("\247r")) || (title.startsWith("\247c\247lGame") && title.endsWith("\247r")) || (title.startsWith("\247c\247lWITH") && title.endsWith("\247r")) || (title.startsWith("\247c\247lYARR") && title.endsWith("\247r"))) {
                timer.reset();
                needSpeak = true;
            }
            lastTitle = title;
        }
    }

}
