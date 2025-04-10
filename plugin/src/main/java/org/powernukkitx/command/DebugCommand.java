package org.powernukkitx.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class DebugCommand extends Command {

    public DebugCommand() {
        super("vanilladebug");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        return super.execute(sender, commandLabel, args);
    }
}
