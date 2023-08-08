package me.pxbz.servercore.modules.warping;

import me.pxbz.servercore.ServerCore;
import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import me.pxbz.servercore.utils.ConfigurationFile;
import me.pxbz.servercore.utils.CoreUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class SetWarpCommand extends BaseCommand {

    public SetWarpCommand() {
        super(SenderType.PLAYER);
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length != 1) return CommandTriState.SEND_USAGE;

        Player p = (Player) sender;
        ConfigurationFile warpsConfig = new ConfigurationFile(new File(ServerCore.getInstance().getDataFolder(), "warps.yml"), ServerCore.getInstance());
        String warpName = args[0].toLowerCase();

        warpsConfig.set(warpName, p.getLocation());
        CoreUtils.sendIfNotBlank(sender, Messages.WARP_SET
                .replaceAll("\\{WARP}", warpName));

        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return Collections.singletonList("<name>");
        return null;
    }
}
