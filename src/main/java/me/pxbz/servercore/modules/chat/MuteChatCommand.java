package me.pxbz.servercore.modules.chat;

import me.pxbz.servercore.Messages;
import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MuteChatCommand extends BaseCommand {

    public MuteChatCommand() {
        super(SenderType.ALL);
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {

        String flags = combineRemainingArgs(0, args).toLowerCase();
        ChatManager.chatMuted = !ChatManager.chatMuted;

        if (ChatManager.chatMuted) {
            CoreUtils.broadcastIfNotBlank(Messages.CHAT_MUTED
                    .replaceAll("\\{BYPLAYER}", flags.contains("-s") ? "" : " by " + sender.getName()));
        }
        else {

            CoreUtils.broadcastIfNotBlank(Messages.CHAT_UNMUTED
                    .replaceAll("\\{BYPLAYER}", flags.contains("-s") ? "" : " by " + sender.getName()));
        }

        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
