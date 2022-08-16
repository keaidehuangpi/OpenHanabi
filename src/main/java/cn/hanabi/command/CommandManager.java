/*
 * Copyright (c) 2018 superblaubeere27
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package cn.hanabi.command;

import cn.hanabi.command.commands.*;
import cn.hanabi.utils.ChatUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


public class CommandManager {
    @NotNull
    private final List<Command> commands = new ArrayList<>();

    public void addCommands() {
        addCommand(new ToggleCommand());
        addCommand(new BindCommand());
        addCommand(new PrefixCommand());
        addCommand(new SpammerCommand());
        addCommand(new CrashCommand());
        addCommand(new BindsCommand());
        addCommand(new ConfigCommand());
        addCommand(new CrashCommand());
        addCommand(new WaypointCommands());
        addCommand(new FriendCommand());
        addCommand(new TargetCommand());
        addCommand(new HideCommand());
        addCommand(new SkinChangeCommand());
    }

    public void addCommand(Command cmd) {
        commands.add(cmd);
    }

    public boolean executeCommand(@NotNull String string) {
        String raw = string.substring(1);
        String[] split = raw.split(" ");

        // HANABI_VERIFY


        if (split.length == 0)
            return false;

        String cmdName = split[0];

        Command command = commands.stream().filter(cmd -> cmd.match(cmdName)).findFirst().orElse(null);

        try {
            if (command == null) {
                return true;
            } else {
                String[] args = new String[split.length - 1];

                System.arraycopy(split, 1, args, 0, split.length - 1);

                command.run(split[0], args);
                return true;
            }
        } catch (CommandException e) {
            ChatUtils.send("\247c" + e.getMessage());
        }
        return true;
    }

    public Collection<String> autoComplete(@NotNull String currCmd) {
        String raw = currCmd.substring(1);
        String[] split = raw.split(" ");

        List<String> ret = new ArrayList<>();

        Command currentCommand = split.length >= 1
                ? commands.stream().filter(cmd -> cmd.match(split[0])).findFirst().orElse(null)
                : null;

        if (split.length >= 2 || currentCommand != null && currCmd.endsWith(" ")) {

            if (currentCommand == null)
                return ret;

            String[] args = new String[split.length - 1];

            System.arraycopy(split, 1, args, 0, split.length - 1);

            List<String> autocomplete = currentCommand.autocomplete(args.length + (currCmd.endsWith(" ") ? 1 : 0),
                    args);

            return autocomplete == null ? new ArrayList<>() : autocomplete;
        } else if (split.length == 1) {
            for (Command command : commands) {
                ret.addAll(command.getNameAndAliases());
            }

            return ret.stream().map(str -> "." + str).filter(str -> str.toLowerCase().startsWith(currCmd.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return ret;
    }
}
