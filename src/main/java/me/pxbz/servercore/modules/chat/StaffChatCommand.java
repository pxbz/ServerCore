package me.pxbz.servercore.modules.chat;

import me.pxbz.servercore.Messages;
import me.pxbz.servercore.utils.ConfigUtils;
import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StaffChatCommand extends BaseCommand {
    private String otherPermission = ConfigUtils.getStr(path + "other-permission");

    public StaffChatCommand() {
        super(SenderType.PLAYER);
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        Player target = (Player) sender;

        if (args.length == 1 && sender.hasPermission(otherPermission)) {
            target = demandValidPlayer(sender, args[0]);
            if (target == null) return CommandTriState.FAILURE;
        }

        ChatManager.toggleStaffChat(target);

        if (target != sender) {
            CoreUtils.sendIfNotBlank(sender, ChatManager.inStaffChat(target)?
                    Messages.STAFF_CHAT_ON_OTHER.replaceAll("\\{TARGET}", target.getName()) :
                    Messages.STAFF_CHAT_OFF_OTHER.replaceAll("\\{TARGET}", target.getName()));
        }

        CoreUtils.sendIfNotBlank(target, ChatManager.inStaffChat(target)?
                Messages.STAFF_CHAT_ON : Messages.STAFF_CHAT_OFF);

        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission(otherPermission)) return matchInput(args[0], getPlayerNames(sender));
        return null;
    }
}
