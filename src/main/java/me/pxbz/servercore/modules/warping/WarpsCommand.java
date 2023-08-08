package me.pxbz.servercore.modules.warping;

import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import me.pxbz.servercore.utils.CoreUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class WarpsCommand extends BaseCommand {

    public WarpsCommand() {
        super(SenderType.ALL);
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        Set<String> warps = WarpingManager.getAvailableWarps(sender).keySet();
        StringBuilder warpsString = new StringBuilder();

        if (warps.isEmpty()) {
            warpsString.append("No available warps.");
        }
        else {
            int counter = 0;
            for (String s : warps) {
                warpsString.append(s);
                if (counter + 1 < warps.size()) warpsString.append(", ");
                counter++;
            }
        }

        CoreUtils.sendIfNotBlank(sender, Messages.WARPS_LIST
                .replaceAll("\\{WARPS}", warpsString.toString()));

        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
