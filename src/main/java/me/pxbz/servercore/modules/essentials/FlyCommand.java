package me.pxbz.servercore.modules.essentials;

import me.pxbz.servercore.utils.ConfigUtils;
import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FlyCommand extends BaseCommand {
    private String flyOtherPermission = ConfigUtils.getStr(path + "other-permission");

    public FlyCommand() {
        super(SenderType.ALL);
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        Player target;
        if (args.length > 0 && sender.hasPermission(flyOtherPermission)) {
            target = demandValidPlayer(sender, args[0]);
            if (target == null) return CommandTriState.FAILURE;
        }
        else {
            if (!demandPlayerSender(sender)) return CommandTriState.FAILURE;
            target = (Player) sender;
        }

        target.setAllowFlight(!target.getAllowFlight());
        CoreUtils.sendIfNotBlank(target, target.getAllowFlight()? Messages.TOGGLE_FLIGHT_ON : Messages.TOGGLE_FLIGHT_OFF);
        if (sender != target) {
            CoreUtils.sendIfNotBlank(sender, target.getAllowFlight() ?
                    Messages.TOGGLE_FLIGHT_ON_OTHER.replaceAll("\\{TARGET}", target.getName()) :
                    Messages.TOGGLE_FLIGHT_OFF_OTHER.replaceAll("\\{TARGET}", target.getName()));
        }

        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return matchInput(args[0], getPlayerNames(sender));
        return null;
    }
}
