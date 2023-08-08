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
import java.util.Set;
import java.util.UUID;

public class GlobalChatCommand extends BaseCommand {

    public GlobalChatCommand() {
        super(SenderType.PLAYER);
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        Player p = (Player) sender;
        UUID uuid = p.getUniqueId();
        Set<UUID> globalChat = ChatManager.getGlobalChat();
        if (globalChat.contains(uuid)) {
            globalChat.remove(uuid);
            CoreUtils.sendIfNotBlank(p, Messages.GLOBAL_CHAT_DISABLED);
        }
        else {
            globalChat.add(uuid);
            CoreUtils.sendIfNotBlank(p, Messages.GLOBAL_CHAT_ENABLED);
        }
        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
