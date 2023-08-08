package me.pxbz.servercore.modules.warping.listeners;

import me.pxbz.servercore.utils.ConfigUtils;
import me.pxbz.servercore.utils.ConfigurationFile;
import me.pxbz.servercore.ServerCore;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;

public class SpawnOnJoinListener implements Listener {
    private final boolean spawnOnJoin = ConfigUtils.getBoolean("modules.warping.spawn-on-join");

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (spawnOnJoin) {
            ConfigurationFile spawnConfig = new ConfigurationFile(new File(ServerCore.getInstance().getDataFolder(), "spawn.yml"), ServerCore.getInstance());
            Location spawn = spawnConfig.getConfig().getLocation("spawn");
            if (spawn == null) {
                spawn = e.getPlayer().getWorld().getSpawnLocation();
                spawnConfig.set("spawn", spawn);
            }
            e.getPlayer().teleport(spawn);
        }
    }
}
