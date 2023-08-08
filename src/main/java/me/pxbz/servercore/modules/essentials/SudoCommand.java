package me.pxbz.servercore.modules.essentials;

import me.pxbz.servercore.utils.ConfigUtils;
import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.command.CraftCommandMap;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SudoCommand extends BaseCommand {
    private String allPermission = ConfigUtils.getStr(path + "all-permission");

    public SudoCommand() {
        super(SenderType.ALL);
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length < 2) return CommandTriState.SEND_USAGE;

        String command = combineRemainingArgs(1, args);

        if (args[0].equalsIgnoreCase("*") || args[0].equalsIgnoreCase("@a")) {
            if (!sender.hasPermission(allPermission)) {
                CoreUtils.sendIfNotBlank(sender, Messages.DEFAULT_PERMISSION_MESSAGE);
                return CommandTriState.FAILURE;
            }

            String input = command.startsWith("c:")? command.substring(2).trim() : command;

            if (command.startsWith("c:")) CoreUtils.sendIfNotBlank(sender, Messages.SUDO_ALL_CHAT
                    .replaceAll("\\{MESSAGE}", input));
            else CoreUtils.sendIfNotBlank(sender, Messages.SUDO_ALL_COMMAND
                    .replaceAll("\\{COMMAND}", command));

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p == sender || EssentialsManager.isVanished(p)) continue;

                if (command.startsWith("c:")) p.chat(input);
                else p.performCommand(input);
            }
        }

        else {
            Player target = demandValidPlayer(sender, args[0]);
            if (target == null) return CommandTriState.FAILURE;

            if (command.startsWith("c:")) {
                String msg = command.substring(2).trim();
                CoreUtils.sendIfNotBlank(sender, Messages.SUDO_CHAT
                        .replaceAll("\\{TARGET}", target.getName())
                        .replaceAll("\\{MESSAGE}", msg));
                target.chat(msg);
            } else {
                CoreUtils.sendIfNotBlank(sender, Messages.SUDO_COMMAND
                        .replaceAll("\\{TARGET}", target.getName())
                        .replaceAll("\\{COMMAND}", command));
                target.performCommand(command);
            }
        }

        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> toReturn = getPlayerNames(sender);
            toReturn.addAll(Arrays.asList("*", "@a"));
            return matchInput(args[0], toReturn);
        }
        else if (args.length == 2) {
            List<String> toReturn = new ArrayList<>(getAvailableCommands(sender));
            toReturn.add("c:");
            return matchInput(args[1], toReturn);
        }
        else if (args.length >= 3) {
            String cmdName = args[1].toLowerCase();
            Map<String, Command> knownCommands = ((CraftCommandMap) ((CraftServer) Bukkit.getServer()).getCommandMap()).getKnownCommands();

            if (knownCommands.containsKey(cmdName) && knownCommands.get(cmdName).testPermissionSilent(sender))
                return knownCommands.get(cmdName).tabComplete(sender, alias, Arrays.copyOfRange(args, 2, args.length));
        }
        return null;
    }
}
