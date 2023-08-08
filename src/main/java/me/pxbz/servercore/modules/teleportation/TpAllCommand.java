package me.pxbz.servercore.modules.teleportation;

import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import me.pxbz.servercore.utils.ConfigUtils;
import me.pxbz.servercore.utils.CoreUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TpAllCommand extends BaseCommand {
    private final String tpallExemptPermission;

    public TpAllCommand() {
        super(SenderType.PLAYER);
        this.tpallExemptPermission = ConfigUtils.getStr(path + "tpall-exempt-permission");
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        Player p = (Player) sender;
        for (Player t : Bukkit.getOnlinePlayers()) {
            if (t == p || !p.canSee(t) || (tpallExemptPermission != null && t.hasPermission(tpallExemptPermission))) continue;
            t.teleport(p);
        }

        CoreUtils.sendIfNotBlank(p, Messages.TELEPORT_A2S);
        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
