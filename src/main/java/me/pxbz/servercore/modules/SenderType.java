package me.pxbz.servercore.modules;

import me.pxbz.servercore.Messages;
import me.pxbz.servercore.utils.CoreUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public enum SenderType {
    PLAYER(),
    CONSOLE(),
    ALL();

    public static SenderType getEnum(String s) {
        if (s == null) return ALL;
        else return valueOf(s.toUpperCase());
    }

    public boolean demandValidSender(CommandSender sender) {
        if (this == PLAYER) {
            if (!(sender instanceof Player)) {
                CoreUtils.sendIfNotBlank(sender, Messages.PLAYER_INVALID_SENDER);
                return false;
            }
        }
        else if (this == CONSOLE) {
            if (!(sender instanceof ConsoleCommandSender)) {
                CoreUtils.sendIfNotBlank(sender, Messages.CONSOLE_INVALID_SENDER);
                return false;
            }
        }
        return true;
    }
}
