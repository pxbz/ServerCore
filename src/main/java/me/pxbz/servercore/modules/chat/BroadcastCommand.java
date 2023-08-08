package me.pxbz.servercore.modules.chat;

import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class BroadcastCommand extends BaseCommand {

    public BroadcastCommand() {
        super(SenderType.ALL);
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length == 0) return CommandTriState.SEND_USAGE;

        String msg = combineRemainingArgs(0, args);
        msg = msg.toLowerCase().startsWith("-p ")? CoreUtils.getPrefix() + " " + ChatManager.colourMessage(msg.substring(3), sender)
                : ChatManager.colourMessage(msg, sender);

        Bukkit.broadcastMessage("\n" + msg + "\n");

        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return matchInput(args[0], Collections.singletonList("-p"));
        return null;
    }
}
