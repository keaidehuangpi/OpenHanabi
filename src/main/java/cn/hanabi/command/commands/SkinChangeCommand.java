package cn.hanabi.command.commands;

import cn.hanabi.command.Command;
import cn.hanabi.utils.ChatUtils;
import me.yarukon.Yarukon;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SkinChangeCommand extends Command {

    public static boolean slim = false;
    public static String targetSkin = "";
    public SkinChangeCommand() {
        super("changeskin");
    }

    @Override
    public void run(String alias, @NotNull String[] args) {
        if(args.length == 1) {
            targetSkin = args[0];
            Yarukon.INSTANCE.loadSkinFromLocal(targetSkin);
            slim = !slim;
            ChatUtils.info("Current skin-type: " + (slim ? "Slim" : "Steve"));
        } else {
            targetSkin = "";
        }
    }

    @Override
    public List<String> autocomplete(int arg, String[] args) {
        return new ArrayList<>();
    }
}
