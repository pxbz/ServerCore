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

import java.util.List;
import java.util.Set;

public class TpHereCommand extends BaseCommand {
    private final String massTeleportPermission;

    public TpHereCommand() {
        super(SenderType.PLAYER);
        massTeleportPermission = ConfigUtils.getStr(path + "mass-tp-permission");
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        Player p = (Player) sender;
        if (args.length == 0) return CommandTriState.SEND_USAGE;

        Player target1 = demandValidPlayer(p, args[0]);
        if (target1 == null) return CommandTriState.FAILURE;

        if (args.length == 1) {
            target1.teleport(p);
            CoreUtils.sendIfNotBlank(p, CoreUtils.formatPlayer(Messages.TELEPORT_P2S, target1));
        }

        else {
            if (!p.hasPermission(massTeleportPermission)) return CommandTriState.SEND_USAGE;

            Set<Player> targets = argsToPlayers(sender, args);
            if (targets == null) return CommandTriState.FAILURE;
            String targetsString = listOfNames(targets);
            for (Player t : targets) t.teleport(p);
            CoreUtils.sendIfNotBlank(p, Messages.TELEPORT_M2S
                    .replaceAll("\\{TARGETS}", targetsString)
                    .replaceAll("\\{TARGET}", target1.getName()));
        }
        return CommandTriState.SUCCESS;
    }

    @Override
    public List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length != 0 && (args.length == 1 || sender.hasPermission(massTeleportPermission)))
            return matchInput(args[args.length - 1], getPlayerNames(sender));
        return null;
    }
}
