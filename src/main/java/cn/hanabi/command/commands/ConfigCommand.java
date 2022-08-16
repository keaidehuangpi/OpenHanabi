package cn.hanabi.command.commands;

import cn.hanabi.Hanabi;
import cn.hanabi.command.Command;
import cn.hanabi.gui.notifications.Notification;
import cn.hanabi.utils.ClientUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConfigCommand extends Command {
    public ConfigCommand() {
        super("config");
    }

    @Override
    public void run(String alias, @NotNull String[] args) {
        if (args.length == 2) {
            if(args[0].equalsIgnoreCase("save")) {
                Hanabi.INSTANCE.fileManager.saveConfig(args[1]);
            }

            if(args[0].equalsIgnoreCase("load")) {
                Hanabi.INSTANCE.fileManager.loadConfig(args[1]);
            }
        }
        else {
            ClientUtil.sendClientMessage(".config [save/load] [name]", Notification.Type.INFO);
        }
    }

    @Override
    public List<String> autocomplete(int arg, String[] args) {
        return new ArrayList<>();
    }


}
