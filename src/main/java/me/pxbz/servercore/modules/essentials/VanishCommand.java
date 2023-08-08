package me.pxbz.servercore.modules.essentials;

import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VanishCommand extends BaseCommand {

    public VanishCommand() {
        super(SenderType.PLAYER);
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        Player p = (Player) sender;

        if (EssentialsManager.isVanished(p)) {
            EssentialsManager.unvanish(p);
            CoreUtils.sendIfNotBlank(sender, Messages.UNVANISHED);
        }
        else {
            EssentialsManager.vanish(p);
            CoreUtils.sendIfNotBlank(sender, Messages.VANISHED);
        }

        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
