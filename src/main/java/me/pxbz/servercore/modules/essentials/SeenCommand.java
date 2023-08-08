package me.pxbz.servercore.modules.essentials;

import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SeenCommand extends BaseCommand {

    public SeenCommand() {
        super(SenderType.ALL);
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length == 0) return CommandTriState.SEND_USAGE;

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore()) {
            CoreUtils.sendIfNotBlank(sender, Messages.PLAYER_NEVER_JOINED
                    .replaceAll("\\{TARGET}", args[0]));
            return CommandTriState.FAILURE;
        }

        CoreUtils.sendIfNotBlank(sender, Messages.SEEN
                .replaceAll("\\{TARGET}", target.getName())
                .replaceAll("\\{LASTPLAYED}", (System.currentTimeMillis() - target.getLastPlayed()) / 1000 + ""));

        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return matchInput(args[0], getPlayerNames(sender));
        return null;
    }
}
