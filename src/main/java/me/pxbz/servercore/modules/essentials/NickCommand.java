package me.pxbz.servercore.modules.essentials;

import me.pxbz.servercore.utils.ConfigUtils;
import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.utils.NMSUtil;
import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NickCommand extends BaseCommand {
    private final String otherPermission = ConfigUtils.getStr(path + "other-permission");

    public NickCommand() {
        super(SenderType.ALL);
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length == 0) return CommandTriState.SEND_USAGE;

        Player target;
        if (args.length < 2) {
            if (!demandPlayerSender(sender)) return CommandTriState.FAILURE;
            target = (Player) sender;
        }
        else {
            if (!sender.hasPermission(otherPermission)) {
                CoreUtils.sendIfNotBlank(sender, Messages.DEFAULT_PERMISSION_MESSAGE);
                return CommandTriState.FAILURE;
            }
            target = demandValidPlayer(sender, args[1]);
            if (target == null) return CommandTriState.FAILURE;
        }

        String oldName = target.getName();
        NMSUtil.setName(target, args[0]);
        target.setPlayerListName(args[0]);
        target.setDisplayName(target.getDisplayName().replace(oldName, args[0]));

        if (sender != target) CoreUtils.sendIfNotBlank(sender, Messages.NICKNAME_SET_OTHER
                .replaceAll("\\{TARGET}", target.getName())
                .replaceAll("\\{NICK}", args[0]));
        CoreUtils.sendIfNotBlank(target, Messages.NICKNAME_SET
                .replaceAll("\\{NICK}", args[0]));

        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 2 && sender.hasPermission(otherPermission)) return matchInput(args[1], getPlayerNames(sender));
        return null;
    }
}
