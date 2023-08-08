package me.pxbz.servercore.modules.moderation;

import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import me.pxbz.servercore.utils.CoreUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TempBanCommand extends BaseCommand {

    public TempBanCommand() {
        super(SenderType.ALL);
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length == 0) return CommandTriState.SEND_USAGE;

        Date expires;
        expires = new Date(System.currentTimeMillis() + 30*60*1000L);
        if (args.length > 1) {
            long duration = CoreUtils.parseDuration(args[1]);
            if (duration != -1) expires = new Date(System.currentTimeMillis() + duration);
            else {
                CoreUtils.sendIfNotBlank(sender, Messages.INVALID_DURATION.replaceAll("\\{DURATION}", args[1]));
            }
        }

        return ModerationManager.tempbanPlayer(sender, args, null, expires);
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return matchInput(args[0], getPlayerNames(sender));
        else if (args.length == 2) return Collections.singletonList("<duration>");
        else return Collections.singletonList("<reason>");
    }
}
