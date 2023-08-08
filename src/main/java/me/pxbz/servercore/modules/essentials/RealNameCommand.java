package me.pxbz.servercore.modules.essentials;

import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RealNameCommand extends BaseCommand {

    public RealNameCommand() {
        super(SenderType.ALL);
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length == 0) return CommandTriState.SEND_USAGE;

        Player target = null;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(args[0])) target = p;
            if (target != null && !target.getName().equals(Bukkit.getOfflinePlayer(target.getUniqueId()).getName())) break;
            // If the target's name is different to their OfflinePlayer equivalent's name, then they are nicked.
            // Get the player whose name is args[0]. If this is also their OfflinePlayer equivalent's name, they are
            // not nicked. Continue searching through the rest of online players, and if a nicked player with this
            // nickname is found, then set the target to them and break loop.
        }

        if (target == null) {
            CoreUtils.sendIfNotBlank(sender, Messages.INVALID_PLAYER.replace("{PLAYER}", args[0]));
            return CommandTriState.FAILURE;
        }

        CoreUtils.sendIfNotBlank(sender, Messages.REAL_NAME
                .replaceAll("\\{NICK}", target.getName())
                .replaceAll("\\{REALNAME}", Bukkit.getOfflinePlayer(target.getUniqueId()).getName()));



        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return matchInput(args[0], getPlayerNames(sender));
        return null;
    }
}
