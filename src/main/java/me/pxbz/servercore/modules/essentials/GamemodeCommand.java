package me.pxbz.servercore.modules.essentials;

import me.pxbz.servercore.utils.ConfigUtils;
import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class GamemodeCommand extends BaseCommand {
    private String gamemodeOtherPermission = ConfigUtils.getStr(path + "other-permission");

    public GamemodeCommand() {
        super(SenderType.ALL);
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length == 0) return CommandTriState.SEND_USAGE;

        Player target;
        if (args.length == 2) {
            target = demandValidPlayer(sender, args[1]);
            if (target == null) return CommandTriState.FAILURE;
        }
        else {
            if (!demandPlayerSender(sender)) return CommandTriState.FAILURE;
            target = (Player) sender;
        }

        if (args[0].equalsIgnoreCase("survival") || args[0].equalsIgnoreCase("s")) target.setGameMode(GameMode.SURVIVAL);
        else if (args[0].equalsIgnoreCase("creative") || args[0].equalsIgnoreCase("c")) target.setGameMode(GameMode.CREATIVE);
        else if (args[0].equalsIgnoreCase("adventure") || args[0].equalsIgnoreCase("a")) target.setGameMode(GameMode.ADVENTURE);
        else if (args[0].equalsIgnoreCase("spectator") || args[0].equalsIgnoreCase("sp")) target.setGameMode(GameMode.SPECTATOR);
        else return CommandTriState.SEND_USAGE;

        if (target == sender) CoreUtils.sendIfNotBlank(sender, Messages.GAMEMODE_CHANGED.replaceAll("\\{GAMEMODE}", target.getGameMode().toString().toLowerCase()));
        else CoreUtils.sendIfNotBlank(sender, Messages.GAMEMODE_CHANGED_OTHER
                .replaceAll("\\{TARGET}", target.getName())
                .replaceAll("\\{GAMEMODE}", target.getGameMode().toString().toLowerCase()));

        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return matchInput(args[0], Arrays.asList("survival", "spectator", "adventure", "creative", "s", "a", "sp", "c"));
        else if (args.length == 2) return matchInput(args[1], getPlayerNames(sender));
        return null;
    }
}
