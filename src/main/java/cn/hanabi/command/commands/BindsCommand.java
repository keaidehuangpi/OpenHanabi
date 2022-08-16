package cn.hanabi.command.commands;

import cn.hanabi.command.Command;
import cn.hanabi.modules.Mod;
import cn.hanabi.modules.ModManager;
import cn.hanabi.utils.PlayerUtil;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class BindsCommand extends Command {
    public BindsCommand() {
        super("binds");
    }

    @Override
    public void run(String alias, @NotNull String[] args) {
        try {
            for (Mod mod : ModManager.modules) {
                if (mod.getKeybind() != 0) {
                    PlayerUtil.tellPlayer("\247b[Hanabi]\247a" + mod.getName() + " - " + Keyboard.getKeyName(mod.getKeybind()));
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public List<String> autocomplete(int arg, String[] args) {
        return new ArrayList<>();
    }
}
