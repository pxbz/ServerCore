package me.pxbz.servercore.modules;

import me.pxbz.servercore.Messages;
import me.pxbz.servercore.exceptions.InvalidConfigPathException;
import me.pxbz.servercore.utils.ConfigUtils;
import me.pxbz.servercore.utils.CoreUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.command.CraftCommandMap;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class BaseCommand extends Command {
    private static final TreeSet<String> serverCoreCommands = new TreeSet<>();
    private final SenderType senderType;
    protected final String path;

    public BaseCommand(SenderType senderType) {
        super("");
        String moduleAndClass = getClass().getCanonicalName().replace("me.pxbz.servercore.modules.", "");
        String module = moduleAndClass.substring(0, moduleAndClass.indexOf("."));
        String command = moduleAndClass.substring(moduleAndClass.indexOf(".") + 1).toLowerCase().replace("command", "");
        path = "modules." + module + ".commands." + command + ".";

        this.senderType = senderType;
        String name;

        try {
            name = ConfigUtils.getNonNullStr(path + "name");
        } catch (InvalidConfigPathException e) {
            name = command;
        }

        setName(name);
        setLabel(name);
        setUsage(ConfigUtils.getStr(path + "usage", "/{LABEL}"));
        setPermission(ConfigUtils.getStr(path + "permission"));
        setPermissionMessage(ConfigUtils.getStr(path + "permission-message"));
        setAliases(ConfigUtils.getStrList(path + "aliases"));
        setDescription(ConfigUtils.getStr(path + "description", ""));
        registerCommand();
    }

    protected static List<String> getPlayerNames(@Nullable CommandSender sender) {
        List<String> toReturn = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (sender instanceof Player
                    && !((Player) sender).canSee(p)) continue;
            toReturn.add(p.getName());
        }
        return toReturn;
    }

    protected static TreeSet<String> getAvailableCommands(@Nullable CommandSender sender) {
        Map<String, Command> knownCommands = ((CraftCommandMap) ((CraftServer) Bukkit.getServer()).getCommandMap()).getKnownCommands();
        TreeSet<String> toReturn = new TreeSet<>();

        if (sender instanceof Player) {
            Player p = (Player) sender;
            for (Map.Entry<String, Command> entry : knownCommands.entrySet()) {
                Command cmd = entry.getValue();
                if (cmd.getPermission() == null || p.hasPermission(cmd.getPermission())) toReturn.add(entry.getKey());
            }
        }
        else toReturn.addAll(knownCommands.keySet());

        return toReturn;
    }

    protected static TreeSet<String> getAvailableServerCoreCommands(@Nullable CommandSender sender) {
        Map<String, Command> knownCommands = ((CraftCommandMap) ((CraftServer) Bukkit.getServer()).getCommandMap()).getKnownCommands();
        TreeSet<String> toReturn = new TreeSet<>();

        if (sender instanceof Player) {
            Player p = (Player) sender;
            for (String s : serverCoreCommands) {
                Command cmd = knownCommands.get(s);
                if (cmd.getPermission() == null || p.hasPermission(cmd.getPermission())) toReturn.add(s);
            }
        }
        else toReturn.addAll(serverCoreCommands);

        return toReturn;
    }

    protected static Command getCommand(String name) {
        return name == null? null : ((CraftServer) Bukkit.getServer()).getCommandMap().getCommand(name);
    }

    protected static List<String> matchInput(@NotNull String input, List<String> possibleCompletions) {
        List<String> toReturn = new ArrayList<>();
        for (String s : possibleCompletions) if (s.toUpperCase().startsWith(input.toUpperCase())) toReturn.add(s);
        return toReturn;
    }

    public static String combineRemainingArgs(int index, String[] args) {
        StringBuilder toReturn = new StringBuilder();
        for (; index < args.length; index++) {
            toReturn.append(args[index]);
            if (index + 1 < args.length) toReturn.append(" ");
        }
        return toReturn.toString();
    }

    public static Player demandValidPlayer(CommandSender sender, String target) {
        Player player = Bukkit.getPlayerExact(target);
        Bukkit.getPlayer("");
        if (player == null || (sender instanceof Player && !((Player) sender).canSee(player))) {
            return null;
        }
        return player;
    }

    public static boolean demandConsoleSender(CommandSender sender) {
        if (!(sender instanceof ConsoleCommandSender)) CoreUtils.sendIfNotBlank(sender, Messages.PLAYER_INVALID_SENDER);
        return sender instanceof ConsoleCommandSender;
    }

    public static boolean demandPlayerSender(CommandSender sender) {
        if (!(sender instanceof Player)) CoreUtils.sendIfNotBlank(sender, Messages.CONSOLE_INVALID_SENDER);
        return sender instanceof Player;
    }

    public static Set<Player> argsToPlayers(CommandSender sender, String[] args) {
        return argsToPlayers(sender, 0, args);
    }

    public static Set<Player> argsToPlayers(CommandSender sender, int index, String[] args) {
        return argsToPlayers(sender, index, args.length, args);
    }

    public static Set<Player> argsToPlayers(CommandSender sender, int index, int end, String[] args) {
        Set<Player> targets = new HashSet<>();
        for (; index < end; index++) {
            Player anotherTarget = BaseCommand.demandValidPlayer(sender, args[index]);
            if (anotherTarget == null) return null;

            targets.add(anotherTarget);
        }
        return targets;
    }

    public static String listOfNames(Collection<Player> players) {
        List<Player> playersList = new ArrayList<>(players);
        StringBuilder names = new StringBuilder();
        for (int x = 0; x < playersList.size(); x++) {
            names.append(playersList.get(x).getName());
            if (x + 1 < players.size()) names.append(", ");
        }
        return names.toString();
    }


    public void registerCommand() {
        CraftCommandMap commandMap = ((CraftCommandMap) ((CraftServer) Bukkit.getServer()).getCommandMap());
        Map<String, Command> knownCommands = commandMap.getKnownCommands();

        knownCommands.put(getLabel(), this);
        serverCoreCommands.add(getLabel());
        for (String alias : getAliases()) {
            knownCommands.put(alias, this);
        }
        register(commandMap);
    }

    public static void unregisterServerCoreCommands() {
        CraftCommandMap commandMap = ((CraftCommandMap) ((CraftServer) Bukkit.getServer()).getCommandMap());
        Map<String, Command> knownCommands = commandMap.getKnownCommands();

        for (String s : new HashSet<>(serverCoreCommands)) {
            if (knownCommands.containsKey(s)) {
                Command cmd = knownCommands.get(s);
                cmd.unregister(commandMap);
                for (String a : cmd.getAliases()) knownCommands.remove(a);
            }
            knownCommands.remove(s);
            serverCoreCommands.remove(s);
        }
    }

    @Override
    public boolean testPermission(@NotNull CommandSender sender) {
        if (testPermissionSilent(sender)) return true;

        if (getPermissionMessage() == null) {
            CoreUtils.sendIfNotBlank(sender, Messages.DEFAULT_PERMISSION_MESSAGE);
        } else if (getPermissionMessage().length() != 0) {
            for (String line : getPermissionMessage().replaceAll("\\{PERMISSION}", getPermission()).split("\n")) {
                sender.sendMessage(line);
            }
        }

        return false;
    }

    @Override
    public final boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!testPermission(sender) || !senderType.demandValidSender(sender)) return false;

        CommandTriState result = executeCommand(sender, commandLabel, args);
        if (result == null) result = CommandTriState.FAILURE;
        if (result == CommandTriState.SEND_USAGE) sender.sendMessage(getUsage().replaceAll("\\{LABEL}", commandLabel));
        return result.value();
    }


    public abstract CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args);

    @Override
    public final @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        List<String> tabCompletions = tabCompleter(sender, alias, args);
        return tabCompletions == null ? new ArrayList<>() : tabCompletions;
    }

    @Nullable
    public abstract List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args);
}
