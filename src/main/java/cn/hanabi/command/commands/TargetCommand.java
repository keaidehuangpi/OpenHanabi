package cn.hanabi.command.commands;

import cn.hanabi.command.Command;
import cn.hanabi.command.CommandException;
import cn.hanabi.utils.ChatUtils;
import cn.hanabi.utils.FriendManager;
import cn.hanabi.utils.TargetManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;



public class TargetCommand extends Command {

	public TargetCommand() {
		super("target");
	}

	@Override
	public void run(String alias, @NotNull String[] args) {
		if (args.length == 0)
			throw new CommandException("Usage: ." + alias + " <add/a/remove/r/clear/c> <name>");

		String option = args[0];

		if (option.equalsIgnoreCase("a") || option.equalsIgnoreCase("add")) {

			boolean isFriendlist = FriendManager.getFriends().contains(args[1]);

			if(!TargetManager.getTarget().contains(args[1]) && !isFriendlist) {
				ChatUtils.success("Added target " + args[1]);
				TargetManager.getTarget().add(args[1]);
			} else {
				ChatUtils.success("This target is already on your " + (isFriendlist ? "Friendlist" : "list" ));
			}
		} else if (option.equalsIgnoreCase("r") || option.equalsIgnoreCase("remove")) {
			if (TargetManager.getTarget().contains(args[1])) {
				TargetManager.getTarget().remove(args[1]);
				ChatUtils.success("Removed target" + args[1]);
			} else {
				ChatUtils.success("This target is already on your list");
			}
		}

		if(option.equalsIgnoreCase("clear" ) || option.equalsIgnoreCase("c" )) {
			ChatUtils.success("Clear Target");
			TargetManager.getTarget().clear();
		}

	}

	@Override
	public List<String> autocomplete(int arg, String[] args) {
		return new ArrayList<>();
	}
}
