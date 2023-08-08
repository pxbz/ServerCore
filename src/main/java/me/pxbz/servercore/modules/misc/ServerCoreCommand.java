package me.pxbz.servercore.modules.misc;

import me.pxbz.servercore.*;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import me.pxbz.servercore.utils.ConfigUtils;
import me.pxbz.servercore.utils.CoreUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.command.CraftCommandMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ServerCoreCommand extends BaseCommand {
    private String helpPermission = CoreUtils.formatServerInfo(ConfigUtils.getStr(path + "help-permission"));
    private String helpMessageHeader = CoreUtils.formatServerInfo(ConfigUtils.getStr(path + "help-message-header"));
    private String helpMessageFormat = CoreUtils.formatServerInfo(ConfigUtils.getStr(path + "help-message-format"));
    private String helpIndividualCommandFormat = CoreUtils.formatServerInfo(ConfigUtils.getStr(path + "help-individual-command-format"));
    private String reloadPermission = CoreUtils.formatServerInfo(ConfigUtils.getStr(path + "reload-permission"));
    private int helpPageLength = Math.max(1, ConfigUtils.getInt(path + "help-page-length"));

    public ServerCoreCommand() {
        super(SenderType.ALL);
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length == 0) return CommandTriState.SEND_USAGE;

        if (args[0].equalsIgnoreCase("help")) {
            if (args.length > 1 && args[1].equalsIgnoreCase("command")) {
                if (args.length < 3) return CommandTriState.SEND_USAGE;

                Map<String, Command> knownCommands = ((CraftCommandMap) ((CraftServer) Bukkit.getServer()).getCommandMap()).getKnownCommands();
                String cmdName = args[2].toLowerCase();
                if (!getAvailableCommands(sender).contains(cmdName)) {
                    CoreUtils.sendIfNotBlank(sender, Messages.DEFAULT_PERMISSION_MESSAGE);
                    return CommandTriState.FAILURE;
                }

                Command cmd = knownCommands.get(cmdName);
                StringBuilder aliases = new StringBuilder();
                int counter = 0;
                for (String s : cmd.getAliases()) {
                    aliases.append(s);
                    if (counter + 1 < cmd.getAliases().size()) aliases.append(", ");
                    counter++;
                }

                CoreUtils.sendIfNotBlank(sender, helpIndividualCommandFormat
                        .replaceAll("\\{COMMAND}", cmd.getName())
                        .replaceAll("\\{DESCRIPTION}", cmd.getDescription())
                        .replaceAll("\\{ALIASES}", aliases.toString().isEmpty() ? "None" : aliases.toString())
                        .replaceAll("\\{USAGE}", cmd.getUsage())
                        .replaceAll("\\{LABEL}", cmd.getName()));
            }
            else {
                int page;
                if (args.length == 1) page = 1;
                else try {page = Integer.parseInt(args[1]);} catch (NumberFormatException e) {return CommandTriState.SEND_USAGE;}

                sendHelpMessage(sender, page);
            }
        }
        else if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission(reloadPermission)) {
                CoreUtils.sendIfNotBlank(sender, Messages.DEFAULT_PERMISSION_MESSAGE);
                return CommandTriState.FAILURE;
            }

            reloadConfig(sender);
        }
        else return CommandTriState.SEND_USAGE;
        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> toReturn = new ArrayList<>();
            if (sender.hasPermission(helpPermission)) toReturn.add("help");
            if (sender.hasPermission(reloadPermission)) toReturn.add("reload");
            return matchInput(args[0], toReturn);
        }
        else if (args.length == 2 && args[0].equalsIgnoreCase("help") && sender.hasPermission(helpPermission)) {
            List<String> toReturn = new ArrayList<>();
            for (int x = getHelpPageAmount(sender); x > 0; x--) {
                toReturn.add(x + "");
            }
            toReturn.add("command");
            return matchInput(args[1], toReturn);
        }
        else if (args.length == 3 && combineRemainingArgs(0, args).toLowerCase().startsWith("help command")) {
            return matchInput(args[2], new ArrayList<>(getAvailableCommands(sender)));
        }
        return null;
    }

    private int getHelpPageAmount(CommandSender sender) {
        int amount = (int) Math.ceil(getAvailableCommands(sender).size() / (double) helpPageLength);
        return Math.max(amount, 1);
    }

    private void sendHelpMessage(CommandSender sender, int page) {
        int helpPageAmount = getHelpPageAmount(sender);
        page = Math.min(Math.max(1, page), helpPageAmount);
        LinkedList<String> availableCommands = new LinkedList<>(getAvailableCommands(sender));

        StringBuilder helpMsg = new StringBuilder(helpMessageHeader
                .replaceAll("\\{PAGE}", page + "")
                .replaceAll("\\{TOTALPAGES}", helpPageAmount + ""));

        for (int x = 0; x < helpPageLength; x++) {
            int commandNumber = (page - 1) * helpPageLength + x;
            if (commandNumber >= availableCommands.size()) break;

            String cmdName = availableCommands.get(commandNumber);
            helpMsg.append("\n").append(helpMessageFormat
                    .replaceAll("\\{NUMBER}", commandNumber + 1 + "")
                    .replaceAll("\\{COMMAND}", cmdName)
                    .replaceAll("\\{DESCRIPTION}", getCommand(cmdName).getDescription()));
        }

        CoreUtils.sendIfNotBlank(sender, helpMsg.toString());
    }

    private void reloadConfig(CommandSender sender) {
        Set<CommandSender> targets = new HashSet<>(Bukkit.getOnlinePlayers());
        targets.add(Bukkit.getConsoleSender());
        for (CommandSender s : targets) {
            if (!s.hasPermission(reloadPermission)) continue;
            CoreUtils.sendIfNotBlank(s, Messages.CONFIG_RELOADING
                    .replaceAll("\\{SENDER}", sender.getName()));
        }

        long start = System.currentTimeMillis();
        ServerCore.getInstance().reloadConfig();
        ConfigUtils.reloadConfig();
        unregisterServerCoreCommands();
        Messages.reloadMessages();
        Permissions.reloadPermissions();
        CommandManager.registerAllCommands();
        EventManager.unregisterEvents();
        EventManager.registerEvents();

        long elapsed = System.currentTimeMillis() - start;
        for (CommandSender s : targets) {
            if (!s.hasPermission(reloadPermission)) continue;
            CoreUtils.sendIfNotBlank(s, Messages.CONFIG_RELOADED
                    .replaceAll("\\{SENDER}", sender.getName())
                    .replaceAll("\\{ELAPSED}", elapsed + ""));
        }
    }
}
