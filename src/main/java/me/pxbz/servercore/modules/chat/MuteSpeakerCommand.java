package me.pxbz.servercore.modules.chat;

import me.pxbz.servercore.Messages;
import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MuteSpeakerCommand extends BaseCommand {

    public MuteSpeakerCommand() {
        super(SenderType.ALL);
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length == 0) return CommandTriState.SEND_USAGE;

        Player target = demandValidPlayer(sender, args[0]);
        if (target == null) return CommandTriState.FAILURE;

        ChatManager.toggleMuteSpeaker(target);

        CoreUtils.broadcastIfNotBlank(ChatManager.isMuteSpeaker(target)?
                Messages.MUTE_SPEAKER_ENABLED.replaceAll("\\{TARGET}", target.getName()) :
                Messages.MUTE_SPEAKER_DISABLED.replaceAll("\\{TARGET}", target.getName()));

        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return matchInput(args[0], getPlayerNames(sender));
        return null;
    }
}
