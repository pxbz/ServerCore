package me.pxbz.servercore.modules.warping;

import me.pxbz.servercore.Messages;
import me.pxbz.servercore.utils.ConfigUtils;
import me.pxbz.servercore.utils.ConfigurationFile;
import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.ServerCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public final class WarpingManager {
    private static final HashMap<UUID, Long> warpCooldown = new HashMap<>();

    private WarpingManager() {}

    public static void timedWarp(Player p, Location location, String warpName) {
        String bypassWarpingDelay = ConfigUtils.getStr("modules.warping.bypass-warp-delay-permission");
        int warpDelay = ConfigUtils.getInt("modules.warping.warp-delay", 0);

        if (p.hasPermission(bypassWarpingDelay) || warpDelay < 1) {
            warp(p, location, warpName);
            return;
        }

        long nextWarpTime = getNextWarpTime(p);
        if (nextWarpTime >= System.currentTimeMillis()) {
            CoreUtils.sendIfNotBlank(p, Messages.ON_WARP_COOLDOWN
                    .replaceAll("\\{TIMELEFT}", (nextWarpTime - System.currentTimeMillis()) / 1000 + ""));
            return;
        }

        CoreUtils.sendIfNotBlank(p, Messages.WARPING_TO_LOCATION
                .replaceAll("\\{WARP}", warpName)
                .replaceAll("\\{DELAY}", warpDelay + ""));
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

                if (counter == warpDelay) {

                    warp(p, location, warpName);
                    cancel();
                    return;
                }
                counter++;
            }
        }.runTaskTimer(ServerCore.getInstance(), 0L, 20L);
    }

    public static void warp(Player p, Location location, String warpName) {
        CoreUtils.sendIfNotBlank(p, Messages.WARPED_TO_LOCATION
                .replaceAll("\\{WARP}", warpName == null ? "location" : warpName));
        normalizeLocation(p, location);
        p.teleport(location);
        setWarpCooldown(p);
    }

    public static void normalizeLocation(Player p, Location loc) {
        loc.setX(loc.getBlockX() + 0.5);
        loc.setZ(loc.getBlockZ() + 0.5);
        loc.setYaw(p.getLocation().getYaw());
        loc.setPitch(p.getLocation().getPitch());
    }

    public static void setWarpCooldown(@NotNull Player p) {
        int cooldown = ConfigUtils.getInt("modules.warping.warp-cooldown", 0);
        if (cooldown == 0) return;

        warpCooldown.put(p.getUniqueId(), System.currentTimeMillis() + cooldown * 1000L);
    }

    public static long getNextWarpTime(@NotNull Player p) {
        return warpCooldown.getOrDefault(p.getUniqueId(), -1L);
    }

    public static HashMap<String, Location> getWarps() {
        ConfigurationFile warpsConfig = new ConfigurationFile(new File(
                ServerCore.getInstance().getDataFolder(), "warps.yml"), ServerCore.getInstance());
        HashMap<String, Location> warps = new HashMap<>();

        for (String k : warpsConfig.getConfig().getKeys(false)) {
            warps.put(k.toLowerCase(), warpsConfig.getConfig().getLocation(k));
        }

        return warps;
    }

    public static HashMap<String, Location> getAvailableWarps(CommandSender sender) {
        ConfigurationFile warpsConfig = new ConfigurationFile(new File(
                ServerCore.getInstance().getDataFolder(), "warps.yml"), ServerCore.getInstance());
        HashMap<String, Location> warps = new HashMap<>();

        for (String k : warpsConfig.getConfig().getKeys(false)) {
            if (!sender.hasPermission("servercore.warping.warp." + k)) continue;
            warps.put(k.toLowerCase(), warpsConfig.getConfig().getLocation(k));
        }

        return warps;
    }
}
