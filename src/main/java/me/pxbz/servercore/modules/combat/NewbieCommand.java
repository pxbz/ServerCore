package me.pxbz.servercore.modules.combat;

import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import me.pxbz.servercore.utils.CoreUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class NewbieCommand extends BaseCommand {

    public NewbieCommand() {
        super(SenderType.PLAYER);
    }

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        Player p = (Player) sender;

        if (args.length == 0) return CommandTriState.SEND_USAGE;

        if (args[0].equalsIgnoreCase("disable")) {
            if (CombatManager.hasNewbieProt(p)) {
                CombatManager.cancelNewbieProt(p);
                CoreUtils.sendIfNotBlank(sender, Messages.NEWBIE_PROT_DISABLED);
            }
            else CoreUtils.sendIfNotBlank(sender, Messages.YOU_DONT_HAVE_NEWBIE_PROT);
        }

        else if (args[0].equalsIgnoreCase("time")) {
            if (CombatManager.hasNewbieProt(p)) {
                CoreUtils.sendIfNotBlank(sender, Messages.NEWBIE_TIME
                        .replaceAll("\\{TIMELEFT}", CombatManager.getNewbieExpireTime(p) / 1000 + ""));
            }
            else CoreUtils.sendIfNotBlank(sender, Messages.YOU_DONT_HAVE_NEWBIE_PROT);

        }
        else return CommandTriState.SEND_USAGE;

        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return matchInput(args[0], Arrays.asList("time", "disable"));
        return null;
    }
}
