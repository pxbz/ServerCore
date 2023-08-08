package me.pxbz.servercore.modules.teleportation;

import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import me.pxbz.servercore.utils.CoreUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TpaToggleCommand extends BaseCommand {

    public TpaToggleCommand() {
        super(SenderType.PLAYER);
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        Player p = (Player) sender;

        TpManager.setTpaEnabled(p, !TpManager.tpaEnabled(p));

        CoreUtils.sendIfNotBlank(sender, TpManager.tpaEnabled(p)? Messages.TPATOGGLE_ON : Messages.TPATOGGLE_OFF);

        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
