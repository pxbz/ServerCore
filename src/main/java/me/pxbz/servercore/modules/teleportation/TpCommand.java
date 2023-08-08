package me.pxbz.servercore.modules.teleportation;

import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import me.pxbz.servercore.utils.ConfigUtils;
import me.pxbz.servercore.utils.CoreUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TpCommand extends BaseCommand {
    private final String massTeleportPermission;

    public TpCommand() {
        super(SenderType.ALL);
        massTeleportPermission = ConfigUtils.getStr(path + "mass-tp-permission");
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length == 0) return CommandTriState.SEND_USAGE;

        Player target1 = demandValidPlayer(sender, args[0]);
        if (target1 == null) return CommandTriState.FAILURE;

        if (args.length == 1) {
            if (!demandPlayerSender(sender)) return CommandTriState.FAILURE;
            Player p = (Player) sender;
            p.teleport(target1);
            CoreUtils.sendIfNotBlank(p, CoreUtils.formatPlayer(Messages.TELEPORT_S2P, target1));
        }

        else if (args.length == 2) {
            Player target2 = demandValidPlayer(sender, args[1]);
            if (target2 == null) return CommandTriState.FAILURE;

            target1.teleport(target2);
            CoreUtils.sendIfNotBlank(sender, Messages.TELEPORT_P2P
                    .replaceAll("\\{TARGET1}", target1.getName())
                    .replaceAll("\\{TARGET2}", target2.getName()));
        }

        else {
            if (!sender.hasPermission(massTeleportPermission)) return CommandTriState.SEND_USAGE;

            Set<Player> targets = argsToPlayers(sender, 1, args);
            if (targets == null) return CommandTriState.FAILURE;
            String targetsString = listOfNames(targets);
            for (Player t : targets) t.teleport(target1);
            CoreUtils.sendIfNotBlank(sender, Messages.TELEPORT_M2P
                    .replaceAll("\\{TARGETS}", targetsString)
                    .replaceAll("\\{TARGET}", target1.getName()));
        }
        return CommandTriState.SUCCESS;
    }

    @Override
    @Nullable
    public List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length != 0 && (args.length <= 2 || sender.hasPermission(massTeleportPermission)))
            return matchInput(args[args.length - 1], getPlayerNames(sender));
        return null;
    }
}
