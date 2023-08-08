package me.pxbz.servercore.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoreUtils {

    private static final Pattern hexPattern = Pattern.compile("&#[A-Fa-f0-9]{6}");
    private static final Pattern durationPattern = Pattern.compile("([0-9]+)((?:second|sec|minute|min|hour|hr|day|week|month|year|yr)s?|mt|[smhdwy])");

    private CoreUtils() {}

    private static String formatServerColours(@NotNull String input) {
        return input.replaceAll("\\{C1}", colour(ConfigUtils.getStr("server-info.primary-colour", "&b")))
                .replaceAll("\\{C2}", colour(ConfigUtils.getStr("server-info.secondary-colour", "&3")))
                .replaceAll("\\{C3}", colour(ConfigUtils.getStr("server-info.tertiary-colour", "&e")));
    }

    public static String formatServerInfo(@NotNull String input) {
        return prefix(formatServerColours(input
                .replaceAll("\\{SERVERNAME}", ConfigUtils.getStr("server-info.name"))
                .replaceAll("\\{COLOUREDSERVER}", getColouredServerName())));
    }

    public static String colour(@NotNull String input) {
        String toReturn = ChatColor.translateAlternateColorCodes('&', input);
        Matcher hexMatcher = hexPattern.matcher(input);
        while (hexMatcher.find()) {
            String foundString = hexMatcher.group();
            toReturn = toReturn.replace(foundString, ChatColor.of(foundString.substring(1)) + "");
        }
        return toReturn;
    }

    public static String uncolour(@NotNull String input) {
        return input.replaceAll("§", "&");
    }

    public static String getPrefix() {
        return colour(formatServerColours(ConfigUtils.getStr("server-info.prefix", "{C1}&lCopeNetwork {C2}&l>{C1}")));
    }

    public static String getServerName() {
        return ConfigUtils.getStr("server-info.name");
    }

    public static String getColouredServerName() {
        return colour(ConfigUtils.getStr("server-info.coloured-name"));
    }

    public static String prefix(@NotNull String input) {
        return input.replace("{PREFIX}", getPrefix());
    }

    public static String formatPlayer(String s, Player p) {
        return s == null ? null : s.replaceAll("\\{TARGET}", p.getName());
    }

    public static void sendIfNotBlank(@NotNull CommandSender sender, String msg) {
        if (msg != null && !msg.isEmpty()) sender.sendMessage(msg);
    }

    public static void broadcastIfNotBlank(String msg) {
        if (msg != null && !msg.isEmpty()) Bukkit.broadcastMessage(msg);
    }

    public static String capitalise(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public static long parseDuration(String text) {
        Matcher m = durationPattern.matcher(text);
        long duration;
        String unit;
        if (m.find()) {
            duration = Integer.parseInt(m.group(1)) * 1000L;
            unit = m.group(2);
        }
        else return -1;

        switch (unit) {
            case "m":
            case "min":
            case "mins":
            case "minute":
            case "minutes":
                duration *= 60;
                break;
            case "h":
            case "hr":
            case "hrs":
            case "hour":
            case "hours":
                duration *= 3600;
                break;
            case "d":
            case "day":
            case "days":
                duration *= 24*3600;
                break;
            case "w":
            case "week":
            case "weeks":
                duration *= 7*24*3600;
                break;
            case "mt":
            case "month":
            case "months":
                duration *= 30*24*3600;
                break;
            case "y":
            case "yr":
            case "yrs":
            case "year":
            case "years":
                duration *= 365*24*3600;
                break;
        }

        return duration;
    }

    public static void sendActionBarIfNotBlank(Player p, String msg) {
        if (msg == null || msg.isEmpty()) return;
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
    }

    public static boolean sameLocation(Location loc1, Location loc2) {
        return loc1.getWorld() == loc2.getWorld() && loc1.getBlockX() == loc2.getBlockX()
                && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ();
    }

    // Player player, String message, String channel, boolean cancelled
    public static void broadcastToDiscordSRV(String msg) {

    }
}
