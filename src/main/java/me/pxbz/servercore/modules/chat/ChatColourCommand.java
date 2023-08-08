package me.pxbz.servercore.modules.chat;

import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatColourCommand extends BaseCommand {
    private static final HashMap<UUID, String> colourPrefixes = new HashMap<>();
    private static final Pattern colourRegex = Pattern.compile("(?:&(?:#[A-Fa-f0-9]{6}|[0-9k-oK-OA-Fa-fr]))+");
    private static final List<String> validColourNames = Arrays.asList("RED", "YELLOW", "GREEN", "AQUA", "BLACK", "WHITE",
            "DARK_RED", "DARK_GREEN", "DARK_AQUA", "LIGHT_PURPLE", "LIGHT_PURPLE", "GOLD", "BLUE",
            "GRAY", "DARK_GRAY");

    public ChatColourCommand() {
        super(SenderType.PLAYER);
    }

    public static String getColourPrefix(Player p) {
        return colourPrefixes.get(p.getUniqueId());
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        Player p = (Player) sender;
        if (args.length == 1) {
            String finalColour;
            if (args[0].equalsIgnoreCase("off")) {
                if (colourPrefixes.containsKey(p.getUniqueId())) {
                    colourPrefixes.remove(p.getUniqueId());
                    CoreUtils.sendIfNotBlank(p, Messages.CHAT_COLOUR_OFF);
                }
                else CoreUtils.sendIfNotBlank(p, Messages.CHAT_COLOUR_OFF_ALREADY);
                return CommandTriState.SUCCESS;
            }
            else if (validColourNames.contains(args[0].toUpperCase())) {
                finalColour = ChatColor.valueOf(args[0].toUpperCase()) + "";
            }

            else {
                Matcher matcher = colourRegex.matcher(args[0]);
                StringBuilder colour = new StringBuilder();
                while (matcher.find()) {
                    colour.append(matcher.group());
                }

                finalColour = CoreUtils.colour(colour.toString());

                if (finalColour.isEmpty()) {
                    CoreUtils.sendIfNotBlank(sender, Messages.INVALID_COLOUR_CODE.replaceAll("\\{INPUT}", args[0]));
                    return CommandTriState.FAILURE;
                }
            }
            colourPrefixes.put(p.getUniqueId(), finalColour);
            CoreUtils.sendIfNotBlank(sender, Messages.CHAT_COLOUR_SET.replaceAll("\\{COLOUR}", finalColour));
        }
        else return CommandTriState.SEND_USAGE;
        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> toCheck = new ArrayList<>(validColourNames);
            toCheck.add("OFF");
            return matchInput(args[0], toCheck);
        }
        return null;
    }
}
