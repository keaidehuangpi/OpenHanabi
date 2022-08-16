package cn.hanabi.command.commands;

import cn.hanabi.command.Command;
import cn.hanabi.command.CommandException;
import cn.hanabi.modules.Mod;
import cn.hanabi.modules.ModManager;
import com.darkmagician6.eventapi.EventManager;

import java.util.ArrayList;
import java.util.List;



public class HideCommand extends Command {
    public HideCommand() {
        super("hide");

        EventManager.register(this);
    }

    @Override
    public void run(String alias, String[] args) {
        if (args.length == 0) {
            throw new CommandException("Usage: ." + alias + " <module> <true , false>");
        }
        Mod mod = ModManager.getModule(args[0], false);

        if (mod == null) throw new CommandException("The module '" + args[0] + "' does not exist");
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("true")) {
                mod.setHidden(true);
            }else if (args[1].equalsIgnoreCase("false")){
                mod.setHidden(false);
            }else {
                throw new CommandException("true or false");
            }
            return;
        }
        mod.setHidden(true);

    }

    @Override
    public List<String> autocomplete(int arg, String[] args) {
        return new ArrayList<>();
    }
}
