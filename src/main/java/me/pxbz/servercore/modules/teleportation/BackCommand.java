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

import java.util.List;
import java.util.Set;

public class BackCommand extends BaseCommand {
    private final String massTeleportPermission;
    private final String backOtherPermission;

    public BackCommand() {
        super(SenderType.ALL);
        massTeleportPermission = ConfigUtils.getStr(path + "mass-tp-permission");
        backOtherPermission = ConfigUtils.getStr(path + "back-other-permission");
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission(backOtherPermission)) {
            Player target1 = demandValidPlayer(sender, args[0]);
            if (target1 == null) return CommandTriState.FAILURE;

            TpManager.back(target1);
            CoreUtils.sendIfNotBlank(sender, Messages.TELEPORT_P2B
                    .replaceAll("\\{TARGET}", target1.getName()));
        }
        else if (args.length > 1 && sender.hasPermission(backOtherPermission) && sender.hasPermission(massTeleportPermission)) {

            Set<Player> targets = argsToPlayers(sender, args);
            if (targets == null) return CommandTriState.FAILURE;
            String targetsString = listOfNames(targets);
            for (Player t : targets) TpManager.back(t);
            CoreUtils.sendIfNotBlank(sender, Messages.TELEPORT_M2B.replaceAll("\\{TARGETS}", targetsString));
        }
        else {
            if (!demandPlayerSender(sender)) return CommandTriState.FAILURE;
            Player p = (Player) sender;
            TpManager.timedBack(p);
        }

        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length >= 1 && sender.hasPermission(backOtherPermission)) {
            if (args.length > 1 && !sender.hasPermission(massTeleportPermission)) return null;

            return matchInput(args[args.length - 1], getPlayerNames(sender));
        }
        return null;
    }


}
