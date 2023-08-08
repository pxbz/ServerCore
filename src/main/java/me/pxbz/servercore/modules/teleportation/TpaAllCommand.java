package me.pxbz.servercore.modules.teleportation;

import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import me.pxbz.servercore.modules.teleportation.teleportrequests.TpaHereRequest;
import me.pxbz.servercore.utils.ConfigUtils;
import me.pxbz.servercore.utils.CoreUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TpaAllCommand extends BaseCommand {
    private String tpaallExemptPermission = ConfigUtils.getStr(path + "tpaall-exempt-permission");

    public TpaAllCommand() {
        super(SenderType.PLAYER);
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        Player p = (Player) sender;

        for (Player t : Bukkit.getOnlinePlayers()) {
            if (t == p || !p.canSee(t) || (tpaallExemptPermission != null && t.hasPermission(tpaallExemptPermission)))
            new TpaHereRequest(p, t);
            CoreUtils.sendIfNotBlank(t, Messages.TPAHERE_RECEIVED.replaceAll("\\{TARGET}", p.getName()));
        }
        CoreUtils.sendIfNotBlank(p, Messages.TPAALL_SENT);

        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
