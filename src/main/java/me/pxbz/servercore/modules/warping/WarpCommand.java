package me.pxbz.servercore.modules.warping;

import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import me.pxbz.servercore.utils.ConfigUtils;
import me.pxbz.servercore.utils.CoreUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WarpCommand extends BaseCommand {
    private String otherPermission = ConfigUtils.getStr(path + "other-permission");

    public WarpCommand() {
        super(SenderType.ALL);
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length == 0) return CommandTriState.SEND_USAGE;


        HashMap<String, Location> warps = WarpingManager.getAvailableWarps(sender);
        String warpName = args[0].toLowerCase();
        if (!warps.containsKey(warpName)) {
            CoreUtils.sendIfNotBlank(sender, Messages.INVALID_WARP
                    .replaceAll("\\{WARP}", warpName));
            return CommandTriState.FAILURE;
        }


        Player target;
        if (args.length == 1 || !sender.hasPermission(otherPermission)) {
            if (!demandPlayerSender(sender)) return CommandTriState.FAILURE;
            target = (Player) sender;
        }
        else {
            target = demandValidPlayer(sender, args[1]);
            if (target == null) return CommandTriState.FAILURE;
        }


        if (target != sender) {
            CoreUtils.sendIfNotBlank(sender, Messages.WARPED_TO_LOCATION_OTHER
                    .replaceAll("\\{WARP}", warpName)
                    .replaceAll("\\{TARGET}", target.getName()));
            target.teleport(warps.get(warpName));
            CoreUtils.sendIfNotBlank(target, Messages.WARPED_TO_LOCATION
                    .replaceAll("\\{WARP}", warpName));
        }
        else WarpingManager.timedWarp(target, warps.get(warpName), warpName);


        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return matchInput(args[0], new ArrayList<>(WarpingManager.getAvailableWarps(sender).keySet()));
        else if (args.length == 2 && sender.hasPermission(otherPermission)) return matchInput(args[0], getPlayerNames(sender));
        return null;
    }
}
