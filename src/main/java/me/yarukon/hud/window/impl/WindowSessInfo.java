package me.yarukon.hud.window.impl;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.Hanabi;
import cn.hanabi.Wrapper;
import cn.hanabi.events.EventPacket;
import cn.hanabi.modules.modules.combat.KillAura;
import cn.hanabi.utils.PacketHelper;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import me.yarukon.hud.window.HudWindow;
import me.yarukon.hud.window.HudWindowManager;
import net.minecraft.network.play.server.S45PacketTitle;


@ObfuscationClass

public class WindowSessInfo extends HudWindow {

    public WindowSessInfo() {
        super("SessionInfo", 5, 25, 125, 80, "Session info", "", 12, 0, 1f);
        EventManager.register(this);
    }

    public static int total = 0;
    public static int win = 0;

    @Override
    public void draw() {

        super.draw();
        //Session time
        long durationInMillis = System.currentTimeMillis() - HudWindowManager.startTime;
        String time;
        if (mc.isSingleplayer()) {
            time = "localhost";
        } else {
            long second = (durationInMillis / 1000) % 60;
            long minute = (durationInMillis / (1000 * 60)) % 60;
            long hour = (durationInMillis / (1000 * 60 * 60)) % 24;
            time = String.format("%02dh %02dm %02ds", hour, minute, second);
        }

        //BPS
        double xDist = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
        double zDist = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;
        double lastDist = StrictMath.sqrt(xDist * xDist + zDist * zDist);
        String bps = String.format("%.2f bps", lastDist * 20.0 * Wrapper.getTimer().timerSpeed);

        //TPS
        double tps = Math.round(PacketHelper.tps * 10) / 10d;

        Hanabi.INSTANCE.fontManager.sessionInfoIcon24.drawString("B", x + 5, y + 16 + 2, textColor);
        Hanabi.INSTANCE.fontManager.usans16.drawString("Play time: " + time, x + 20, y + 18, textColor);

        Hanabi.INSTANCE.fontManager.sessionInfoIcon24.drawString("C", x + 5, y + 32 + 2, textColor);
        Hanabi.INSTANCE.fontManager.usans16.drawString("Move speed: " + bps, x + 20, y + 33, textColor);

        Hanabi.INSTANCE.fontManager.sessionInfoIcon24.drawString("D", x + 4, y + 46 + 2, textColor);
        Hanabi.INSTANCE.fontManager.usans16.drawString("Win / Total: " + win + " / " + total, x + 20, y + 48, textColor);

        Hanabi.INSTANCE.fontManager.sessionInfoIcon24.drawString("E", x + 4, y + 60 + 2, textColor);
        Hanabi.INSTANCE.fontManager.usans16.drawString("TPS: " + tps, x + 20, y + 63f, textColor);

        Hanabi.INSTANCE.fontManager.sessionInfoIcon20.drawString("F", x + 5, y + 76 + 2, textColor);
        Hanabi.INSTANCE.fontManager.usans16.drawString("Kills: " + KillAura.killCount, x + 20, y + 77f, textColor);
    }

    @EventTarget
    public void onPacket(EventPacket evt) {
        if (evt.getPacket() instanceof S45PacketTitle) {
            S45PacketTitle packet = (S45PacketTitle) evt.getPacket();
            String title = packet.getMessage().getFormattedText();
            if ((title.startsWith("\2476\247l") && title.endsWith("\247r")) || (title.startsWith("\247c\247lYOU") && title.endsWith("\247r")) || (title.startsWith("\247c\247lGame") && title.endsWith("\247r")) || (title.startsWith("\247c\247lWITH") && title.endsWith("\247r")) || (title.startsWith("\247c\247lYARR") && title.endsWith("\247r"))) {
                total++;
            }

            if (title.startsWith("\2476\247l") && title.endsWith("\247r")) {
                win++;
            }
        }
    }
}
