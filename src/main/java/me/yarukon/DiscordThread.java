package me.yarukon;

import cn.hanabi.Client;
import cn.hanabi.Hanabi;
import cn.hanabi.modules.modules.combat.KillAura;
import cn.hanabi.utils.TimeHelper;
import me.yarukon.hud.window.HudWindowManager;
import me.yarukon.hud.window.impl.WindowSessInfo;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.client.Minecraft;

public class DiscordThread extends Thread {
	public final static String DISCORD_ID = "898551434608533555";

	public boolean isDiscordRunning = false;

	public TimeHelper timer = new TimeHelper();

	@Override
	public void run() {
		//重置计时器
		timer.reset();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			Hanabi.INSTANCE.println("Closing Discord hook.");
			this.isDiscordRunning = false;
			DiscordRPC.discordShutdown();
		}));

		DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
			Hanabi.INSTANCE.println("Found Discord: " + user.username + "#" + user.discriminator + ".");
			this.isDiscordRunning = true;
		}).build();

		DiscordRPC.discordInitialize(DISCORD_ID, handlers, true);
		DiscordRPC.discordRegister(DISCORD_ID, "");

		while (true) {
			DiscordRPC.discordRunCallbacks();

			if (this.isDiscordRunning) {
				int killed = KillAura.killCount;
				int win = WindowSessInfo.win;
				DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder( "Killed: " + killed + " Won:" + win);
				presence.setBigImage("logo", "Hanabi " + Hanabi.CLIENT_VERSION + " [" + (Client.rank.equals("release") ? "Release" : "Beta") + "]");
				presence.setDetails("User: " + Client.username);

				if(!Minecraft.getMinecraft().isSingleplayer() && Minecraft.getMinecraft().getCurrentServerData() != null) {
					String ip = Minecraft.getMinecraft().getCurrentServerData().serverIP;
					ip = ip.contains(":") ? ip : ip + ":25565";
					boolean isHypickle = ip.toLowerCase().contains("hypixel");
					presence.setSmallImage(isHypickle ? "hypickle" : "server", isHypickle ? "mc.hypixel.net:25565" : ip);
					presence.setStartTimestamps(HudWindowManager.startTime / 1000);
				}

				DiscordRPC.discordUpdatePresence(presence.build());
				
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			} else {
				if(this.timer.isDelayComplete(10000)) {
					Hanabi.INSTANCE.println("Timeout while finding Discord process! exiting...");
					break;
				}
			}
		}
	}
}
