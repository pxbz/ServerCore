package me.pxbz.servercore.modules.teleportation;

import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import me.pxbz.servercore.modules.teleportation.teleportrequests.AbstractTeleportRequest;
import me.pxbz.servercore.modules.teleportation.teleportrequests.TpaHereRequest;
import me.pxbz.servercore.utils.CoreUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TpaHereCommand extends BaseCommand {


    public TpaHereCommand() {
        super(SenderType.PLAYER);
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length == 0) return CommandTriState.SEND_USAGE;
        Player p = (Player) sender;

        Player target = demandValidPlayer(p, args[0]);
        if (target == null) return CommandTriState.FAILURE;

        AbstractTeleportRequest lastTpRequest = AbstractTeleportRequest.getIncomingRequest(target, p);
        if (lastTpRequest != null) {
            if (lastTpRequest instanceof TpaHereRequest) {
                CoreUtils.sendIfNotBlank(p, Messages.ALREADY_SENT_TPAHERE.replaceAll("\\{TARGET}", target.getName()));
                return CommandTriState.FAILURE;
            }
            else lastTpRequest.endTeleportRequest();
        }

        if (!TpManager.demandTpaEnabled(sender, target)) return CommandTriState.FAILURE;

        new TpaHereRequest(p, target);
        CoreUtils.sendIfNotBlank(p, Messages.TPAHERE_SENT.replaceAll("\\{TARGET}", target.getName()));
        CoreUtils.sendIfNotBlank(target, Messages.TPAHERE_RECEIVED.replaceAll("\\{TARGET}", p.getName()));

        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return matchInput(args[0], getPlayerNames(sender));
        return null;
    }
}
