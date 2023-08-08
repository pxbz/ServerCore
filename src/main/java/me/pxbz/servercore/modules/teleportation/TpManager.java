package me.pxbz.servercore.modules.teleportation;

import me.pxbz.servercore.ServerCore;
import me.pxbz.servercore.Messages;
import me.pxbz.servercore.utils.ConfigUtils;
import me.pxbz.servercore.utils.CoreUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class TpManager {
    private static final HashMap<UUID, Location> backLocations = new HashMap<>();
    private static final Set<UUID> tpaDisabled = new HashSet<>();

    private TpManager() {}

    public static void setTpaEnabled(Player p, boolean status) {
        if (status) tpaDisabled.remove(p.getUniqueId());
        else tpaDisabled.add(p.getUniqueId());
    }

    public static boolean tpaEnabled(Player p) {
        return !tpaDisabled.contains(p.getUniqueId());
    }

    public static boolean demandTpaEnabled(@NotNull CommandSender sender, @NotNull Player p) {
        if (!tpaEnabled(p)) CoreUtils.sendIfNotBlank(sender, Messages.TP_REQUESTS_DISABLED
                .replaceAll("\\{TARGET}", p.getName()));
        return tpaEnabled(p);
    }

    private static void setBackLocation(Player p) {
        setBackLocation(p, p.getLocation());
    }

    public static void setBackLocation(Player p, Location loc) {
        backLocations.put(p.getUniqueId(), loc);
    }

    public static Location getBackLocation(Player p) {
        return backLocations.get(p.getUniqueId());
    }

    public static void back(Player p) {
        Location backLoc = getBackLocation(p);
        if (backLoc == null) backLoc = p.getLocation();
        setBackLocation(p);
        p.teleport(backLoc);
        CoreUtils.sendIfNotBlank(p, Messages.TELEPORT_BACK);
    }

    public static void timedBack(Player p) {
        String bypassTeleportDelay = ConfigUtils.getStr("modules.teleportation.bypass-teleport-delay-permission");
        int teleportDelay = ConfigUtils.getInt("modules.teleportation.teleport-delay", 0);
        if (p.hasPermission(bypassTeleportDelay) || teleportDelay < 1) {
            back(p);
            return;
        }

        CoreUtils.sendIfNotBlank(p, Messages.TELEPORTING_BACK
                .replaceAll("\\{DELAY}", teleportDelay + ""));
        UUID uuid = p.getUniqueId();
        Location originalSpot = p.getLocation();
        new BukkitRunnable() {
            Player p;
            int counter = 0;
            @Override
            public void run() {
                p = Bukkit.getPlayer(uuid);
                if (p == null) {
                    cancel();
                    return;
                }
                if (!CoreUtils.sameLocation(originalSpot, p.getLocation())) {
                    CoreUtils.sendIfNotBlank(p, Messages.TELEPORT_CANCELLED_MOVEMENT);
                    cancel();
                    return;
                }

                if (counter == teleportDelay) {

                    back(p);
                    cancel();
                    return;
                }
                counter++;
            }
        }.runTaskTimer(ServerCore.getInstance(), 0L, 20L);
    }
}
