package me.pxbz.servercore.modules.chat;

import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.ServerCore;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class BroadcastGlobalCommand extends BaseCommand {

    public BroadcastGlobalCommand() {
        super(SenderType.ALL);
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length == 0) return CommandTriState.SEND_USAGE;

        String msg = combineRemainingArgs(0, args);
        msg = msg.toLowerCase().startsWith("-p ")? CoreUtils.colour("&b&lCopeNetwork &3&l>&r") + " " + ChatManager.colourMessage(msg.substring(3), sender)
                : ChatManager.colourMessage(msg, sender);

        ServerCore.getSubserver().broadcast(msg);

        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return matchInput(args[0], Collections.singletonList("-p"));
        return null;
    }
}
