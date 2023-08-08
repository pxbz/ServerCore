package me.pxbz.servercore.modules.teleportation;

import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.BaseCommand;
import me.pxbz.servercore.utils.ConfigUtils;
import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.modules.CommandTriState;
import me.pxbz.servercore.modules.SenderType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class RtpCommand extends BaseCommand {
    private static final Random random = new Random();
    private int radius = ConfigUtils.getInt(path + "radius");
    private int cooldownDuration = ConfigUtils.getInt(path + "cooldown");
    private static final HashMap<UUID, Long> cooldownExpire = new HashMap<>();

    public RtpCommand() {
        super(SenderType.PLAYER);
    }

    // TO DO:
    // make sure the destination is safe
    // make sure the destination is not in water

    @Override
    public CommandTriState executeCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        Player p = (Player) sender;

        if (cooldownExpire.containsKey(p.getUniqueId())) {
            if (cooldownExpire.get(p.getUniqueId()) < System.currentTimeMillis()) {
                long timeRemainingSecs = (System.currentTimeMillis() - cooldownExpire.get(p.getUniqueId())) / 1000;
                CoreUtils.sendIfNotBlank(sender, Messages.COMMAND_ON_COOLDOWN
                        .replaceAll("\\{TIMEREMAINING}", timeRemainingSecs + "")
                        .replaceAll("\\{LABEL}", commandLabel));
                return CommandTriState.FAILURE;
            }
            else cooldownExpire.remove(p.getUniqueId());
        }

        Block underPlayer = null;

        while (underPlayer == null || underPlayer.getY() < -63 || underPlayer.getType() == Material.AIR) {
            int x = random.nextInt(radius * 2) - radius;
            int z = random.nextInt(radius * 2) - radius;
            underPlayer = p.getLocation().getWorld().getHighestBlockAt(x, z);
        }

        p.teleport(new Location(p.getLocation().getWorld(), underPlayer.getX(), underPlayer.getY() + 1, underPlayer.getZ()));
        CoreUtils.sendIfNotBlank(sender, Messages.RTP_TELEPORTED);

        cooldownExpire.put(p.getUniqueId(), System.currentTimeMillis() + cooldownDuration*1000L);

        return CommandTriState.SUCCESS;
    }

    @Override
    public @Nullable List<String> tabCompleter(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
