package me.pxbz.servercore.modules.warping;

import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import me.pxbz.servercore.utils.ConfigUtils;
import me.pxbz.servercore.utils.ConfigurationFile;
import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.ServerCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public class SpawnCommand extends BaseCommand {
    private String otherPermission = ConfigUtils.getStr(path + "other-permission");

    public SpawnCommand() {
        super(SenderType.ALL);
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        Player target;

        if (args.length == 0 || !sender.hasPermission(otherPermission)) {
            if (!demandPlayerSender(sender)) return CommandTriState.FAILURE;
            target = (Player) sender;
        }
        else {
            target = demandValidPlayer(sender, args[0]);
            if (target == null) return CommandTriState.FAILURE;
        }


        Location spawn = new ConfigurationFile(new File(ServerCore.getInstance().getDataFolder(), "spawn.yml"), ServerCore.getInstance())
                .getConfig().getLocation("spawn", Bukkit.getWorlds().iterator().next().getSpawnLocation());
        WarpingManager.timedWarp(target, spawn, "spawn");
        if (sender != target) CoreUtils.sendIfNotBlank(sender, Messages.TP_TO_SPAWN_OTHER.replaceAll("\\{TARGET}", target.getName()));
        CoreUtils.sendIfNotBlank(target, Messages.TP_TO_SPAWN);

        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission(otherPermission)) return matchInput(args[0], getPlayerNames(sender));
        return null;
    }
}
