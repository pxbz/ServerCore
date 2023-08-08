package me.pxbz.servercore.modules.moderation;

import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.utils.CoreUtils;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public final class ModerationManager {

    private ModerationManager() {}

    public static CommandTriState kickPlayer(CommandSender sender, String[] args, String kickSilentNotifyPermission) {
        if (args.length == 0) return CommandTriState.SEND_USAGE;

        Player target = BaseCommand.demandValidPlayer(sender, args[0]);
        if (target == null) return CommandTriState.FAILURE;

        String reason = BaseCommand.combineRemainingArgs(1, args);
        target.kickPlayer(formatKickMessage(Messages.KICK_MESSAGE, reason, sender, target));

        if (kickSilentNotifyPermission != null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission(kickSilentNotifyPermission))
                    CoreUtils.sendIfNotBlank(p, formatKickMessage(Messages.SILENT_KICK_MESSAGE, reason, sender, target));
            }
        }
        else CoreUtils.broadcastIfNotBlank(formatKickMessage(Messages.ANNOUNCED_KICK_MESSAGE, reason, sender, target));

        return CommandTriState.SUCCESS;
    }

    private static String formatKickMessage(@NotNull String input, @NotNull String reason, CommandSender sender, Player target) {
        return CoreUtils.formatServerInfo(input)
                .replaceAll("\\{BYPLAYER}", sender instanceof Player && target.canSee((Player) sender)?
                        " by " + sender.getName() : "")
                .replaceAll("\\{KICKEDPLAYER}", target.getName())
                .replaceAll("\\{REASON}", reason.isEmpty()?
                        "" : ": " + CoreUtils.colour(reason));
    }

    public static CommandTriState banPlayer(CommandSender sender, String[] args, String banSilentNotificationPermission) {
        if (args.length == 0) return CommandTriState.SEND_USAGE;

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        String reason = BaseCommand.combineRemainingArgs(1, args);
        String banMessage = formatBanMessage(Messages.BAN_MESSAGE, reason, sender, target);
        Bukkit.getBanList(BanList.Type.NAME).addBan(target.getName(), banMessage, null, sender.getName());
        if (target.getPlayer() != null) target.getPlayer().kickPlayer(banMessage);

        if (banSilentNotificationPermission != null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission(banSilentNotificationPermission))
                    CoreUtils.sendIfNotBlank(p, formatBanMessage(Messages.SILENT_BAN_MESSAGE, reason, sender, target));
            }
        }
        else CoreUtils.broadcastIfNotBlank(formatBanMessage(Messages.ANNOUNCED_BAN_MESSAGE, reason, sender, target));

        return CommandTriState.SUCCESS;
    }

    private static String formatBanMessage(@NotNull String input, @NotNull String reason, CommandSender sender, OfflinePlayer target) {
        return CoreUtils.formatServerInfo(input)
                .replaceAll("\\{BYPLAYER}", " by " + sender.getName())
                .replaceAll("\\{BANNEDPLAYER}", target.getName())
                .replaceAll("\\{REASON}", reason.isEmpty()?
                        "" : ": " + CoreUtils.colour(reason));
    }

    public static CommandTriState tempbanPlayer(CommandSender sender, String[] args, String banSilentNotificationPermission, Date expire) {
        if (args.length == 0) return CommandTriState.SEND_USAGE;

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        String reason = BaseCommand.combineRemainingArgs(2, args);
        String banMessage = formatTempbanMessage(Messages.TEMPBAN_MESSAGE, reason, sender, target, args[1]);
        Bukkit.getBanList(BanList.Type.NAME).addBan(target.getName(), banMessage, expire, sender.getName());
        if (target.getPlayer() != null) target.getPlayer().kickPlayer(banMessage);

        if (banSilentNotificationPermission != null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission(banSilentNotificationPermission))
                    CoreUtils.sendIfNotBlank(p, formatTempbanMessage(Messages.SILENT_TEMPBAN_MESSAGE, reason, sender, target, args[1]));
            }
        }
        else CoreUtils.broadcastIfNotBlank(formatTempbanMessage(Messages.ANNOUNCED_TEMPBAN_MESSAGE, reason, sender, target, args[1]));

        return CommandTriState.SUCCESS;
    }

    private static String formatTempbanMessage(@NotNull String input, @NotNull String reason, CommandSender sender, OfflinePlayer target, String duration) {
        return CoreUtils.formatServerInfo(input)
                .replaceAll("\\{BYPLAYER}", " by " + sender.getName())
                .replaceAll("\\{BANNEDPLAYER}", target.getName())
                .replaceAll("\\{REASON}", reason.isEmpty()?
                        "" : ": " + CoreUtils.colour(reason))
                .replaceAll("\\{DURATION}", duration == null? " permanently" : " for " + duration);
    }

    public static CommandTriState unbanPlayer(CommandSender sender, String[] args, String unbanSilentNotificationPermission) {
        if (args.length == 0) return CommandTriState.SEND_USAGE;

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (!target.isBanned()) {
            CoreUtils.sendIfNotBlank(sender, Messages.PLAYER_NOT_BANNED.replaceAll("\\{TARGET}", target.getName()));
            return CommandTriState.FAILURE;
        }
        Bukkit.getBanList(BanList.Type.NAME).pardon(target.getName());

        if (unbanSilentNotificationPermission != null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission(unbanSilentNotificationPermission))
                    CoreUtils.sendIfNotBlank(p, formatUnbanMessage(Messages.SILENT_UNBAN_MESSAGE, sender, target));
            }
        }
        else CoreUtils.broadcastIfNotBlank(formatUnbanMessage(Messages.ANNOUNCED_UNBAN_MESSAGE, sender, target));

        return CommandTriState.SUCCESS;
    }

    private static String formatUnbanMessage(@NotNull String input, CommandSender sender, OfflinePlayer target) {
        return CoreUtils.formatServerInfo(input)
                .replaceAll("\\{BYPLAYER}", " by " + sender.getName())
                .replaceAll("\\{UNBANNEDPLAYER}", target.getName());
    }

//    public static CommandTriState banIp(CommandSender sender, String[] args, String banSilentNotificationPermission) {
//        if (args.length == 0) return CommandTriState.SEND_USAGE;
//
//        Player target = BaseCommand.demandValidPlayer(sender, args[0]);
//        if (target == null) return CommandTriState.FAILURE;
//
//        String reason = BaseCommand.combineRemainingArgs(1, args);
//        String banMessage = formatBanMessage(Messages.BAN_MESSAGE, reason, sender, target);
//        Bukkit.getBanList(BanList.Type.IP).addBan(target.getAddress().getHostName(), banMessage, null, sender.getName());
//        if (target.getPlayer() != null) target.getPlayer().kickPlayer(banMessage);
//
//        if (banSilentNotificationPermission != null) {
//            for (Player p : Bukkit.getOnlinePlayers()) {
//                if (p.hasPermission(banSilentNotificationPermission))
//                    CoreUtils.sendIfNotBlank(p, formatBanMessage(Messages.SILENT_BAN_MESSAGE, reason, sender, target));
//            }
//        }
//        else CoreUtils.broadcastIfNotBlank(formatBanMessage(Messages.ANNOUNCED_BAN_MESSAGE, reason, sender, target));
//
//        return CommandTriState.SUCCESS;
//    }
//
//    private static String formatBanIpMessage(@NotNull String input, @NotNull String reason, CommandSender sender, OfflinePlayer target) {
//        return CoreUtils.formatServerInfo(input)
//                .replaceAll("\\{BYPLAYER}", " by " + sender.getName())
//                .replaceAll("\\{BANNEDPLAYER}", target.getName())
//                .replaceAll("\\{REASON}", reason.isEmpty()?
//                        "" : ": " + CoreUtils.colour(reason));
//    }
//
//    public static CommandTriState tempbanIp(CommandSender sender, String[] args, String banSilentNotificationPermission, Date expire) {
//        if (args.length == 0) return CommandTriState.SEND_USAGE;
//
//        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
//
//        String reason = BaseCommand.combineRemainingArgs(2, args);
//        String banMessage = formatTempbanMessage(Messages.TEMPBAN_MESSAGE, reason, sender, target, args[1]);
//        Bukkit.getBanList(BanList.Type.NAME).addBan(target.getName(), banMessage, expire, sender.getName());
//        if (target.getPlayer() != null) target.getPlayer().kickPlayer(banMessage);
//
//        if (banSilentNotificationPermission != null) {
//            for (Player p : Bukkit.getOnlinePlayers()) {
//                if (p.hasPermission(banSilentNotificationPermission))
//                    CoreUtils.sendIfNotBlank(p, formatTempbanMessage(Messages.SILENT_TEMPBAN_MESSAGE, reason, sender, target, args[1]));
//            }
//        }
//        else CoreUtils.broadcastIfNotBlank(formatTempbanMessage(Messages.ANNOUNCED_TEMPBAN_MESSAGE, reason, sender, target, args[1]));
//
//        return CommandTriState.SUCCESS;
//    }
//
//    private static String formatTempbanIpMessage(@NotNull String input, @NotNull String reason, CommandSender sender, OfflinePlayer target, String duration) {
//        return CoreUtils.formatServerInfo(input)
//                .replaceAll("\\{BYPLAYER}", " by " + sender.getName())
//                .replaceAll("\\{BANNEDPLAYER}", target.getName())
//                .replaceAll("\\{REASON}", reason.isEmpty()?
//                        "" : ": " + CoreUtils.colour(reason))
//                .replaceAll("\\{DURATION}", duration == null? " permanently" : " for " + duration);
//    }
//
//    public static CommandTriState unbanIp(CommandSender sender, String[] args, String unbanSilentNotificationPermission) {
//        if (args.length == 0) return CommandTriState.SEND_USAGE;
//
//        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
//
//        if (!target.isBanned()) {
//            CoreUtils.sendIfNotBlank(sender, Messages.PLAYER_NOT_BANNED.replaceAll("\\{TARGET}", target.getName()));
//            return CommandTriState.FAILURE;
//        }
//        Bukkit.getBanList(BanList.Type.NAME).pardon(target.getName());
//
//        if (unbanSilentNotificationPermission != null) {
//            for (Player p : Bukkit.getOnlinePlayers()) {
//                if (p.hasPermission(unbanSilentNotificationPermission))
//                    CoreUtils.sendIfNotBlank(p, formatUnbanMessage(Messages.SILENT_UNBAN_MESSAGE, sender, target));
//            }
//        }
//        else CoreUtils.broadcastIfNotBlank(formatUnbanMessage(Messages.ANNOUNCED_UNBAN_MESSAGE, sender, target));
//
//        return CommandTriState.SUCCESS;
//    }
//
//    private static String formatUnbanIpMessage(@NotNull String input, CommandSender sender, OfflinePlayer target) {
//        return CoreUtils.formatServerInfo(input)
//                .replaceAll("\\{BYPLAYER}", " by " + sender.getName())
//                .replaceAll("\\{UNBANNEDPLAYER}", target.getName());
//    }
}
