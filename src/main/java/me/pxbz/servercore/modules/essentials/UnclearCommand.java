package me.pxbz.servercore.modules.essentials;

import me.pxbz.servercore.utils.ConfigUtils;
import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UnclearCommand extends BaseCommand {
    private String otherPermission = ConfigUtils.getStr(path + "other-permission");

    public UnclearCommand() {
        super(SenderType.ALL);
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        Player target;
        if (args.length == 0) {
            if (!demandPlayerSender(sender)) return CommandTriState.FAILURE;
            target = (Player) sender;
        }
        else {
            if (!sender.hasPermission(otherPermission)) {
                CoreUtils.sendIfNotBlank(sender, Messages.DEFAULT_PERMISSION_MESSAGE);
                return CommandTriState.FAILURE;
            }

            target = demandValidPlayer(sender, args[0]);
            if (target == null) return CommandTriState.FAILURE;
        }

        ItemStack[] items = EssentialsManager.getClearedItems(target);
        if (items == null) {
            CoreUtils.sendIfNotBlank(sender, sender == target? Messages.UNCLEAR_ITEMS_EMPTY :
                    Messages.UNCLEAR_ITEMS_EMPTY_OTHER.replaceAll("\\{TARGET}", target.getName()));
            return CommandTriState.FAILURE;
        }

        EssentialsManager.setClearedItems(target, target.getInventory().getContents());
        target.getInventory().setContents(items);
        if (target != sender) CoreUtils.sendIfNotBlank(sender, Messages.UNCLEARED_INVENTORY_OTHER.replaceAll("\\{TARGET}", target.getName()));
        CoreUtils.sendIfNotBlank(target, Messages.UNCLEARED_INVENTORY);

        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission(otherPermission)) return matchInput(args[0], getPlayerNames(sender));
        return null;
    }
}
