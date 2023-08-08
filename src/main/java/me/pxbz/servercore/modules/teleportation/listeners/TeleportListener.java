package me.pxbz.servercore.modules.teleportation.listeners;

import me.pxbz.servercore.modules.teleportation.TpManager;
import me.pxbz.servercore.utils.ConfigUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.UUID;

public class TeleportListener implements Listener {
    private final HashMap<UUID, Long> lastTpTime = new HashMap<>();
    private final int tpInvincibility = ConfigUtils.getInt("modules.teleportation.tp-invincibility-duration", 0);

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getCause() != PlayerTeleportEvent.TeleportCause.COMMAND
                && e.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) return;
        if (e.isCancelled()) return;

        TpManager.setBackLocation(e.getPlayer(), e.getFrom());
        if (tpInvincibility > 0) lastTpTime.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntityType() != EntityType.PLAYER && e.getDamager().getType() != EntityType.PLAYER) return;
        if (e.isCancelled()) return;

        UUID uuid = e.getEntity().getUniqueId();
        if (lastTpTime.containsKey(uuid)) {
            if (lastTpTime.get(uuid) + tpInvincibility * 1000L > System.currentTimeMillis()) e.setCancelled(true);
            else lastTpTime.remove(uuid);
        }
    }
}
