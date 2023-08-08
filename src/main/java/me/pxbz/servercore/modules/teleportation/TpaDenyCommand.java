package me.pxbz.servercore.modules.teleportation;

import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import me.pxbz.servercore.modules.teleportation.teleportrequests.AbstractTeleportRequest;
import me.pxbz.servercore.utils.CoreUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TpaDenyCommand extends BaseCommand {

    public TpaDenyCommand() {
        super(SenderType.PLAYER);
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        Player p = (Player) sender;

        AbstractTeleportRequest tpRequest;

        if (args.length == 0) tpRequest = AbstractTeleportRequest.getLastIncomingRequest(p);
        else if (args.length == 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            tpRequest = AbstractTeleportRequest.getIncomingRequest(p, target);
        }
        else return CommandTriState.SEND_USAGE;

        if (tpRequest == null) {
            CoreUtils.sendIfNotBlank(p, Messages.TPACCEPT_NO_INCOMING_REQUESTS);
            return CommandTriState.FAILURE;
        }

        tpRequest.deny();

        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return matchInput(args[0], getPlayerNames(sender));
        return null;
    }
}
