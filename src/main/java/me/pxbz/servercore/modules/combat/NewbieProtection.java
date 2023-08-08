package me.pxbz.servercore.modules.combat;

import me.pxbz.servercore.ServerCore;
import me.pxbz.servercore.Messages;
import me.pxbz.servercore.utils.CoreUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class NewbieProtection {

    private final UUID uuid;
    private final int duration;
    private final long expireTime;
    private final BukkitTask expireTask;
    private boolean cancelled = false;

    public NewbieProtection(Player newbie, int duration) {
        this.uuid = newbie.getUniqueId();
        this.duration = duration;
        this.expireTime = System.currentTimeMillis() + duration * 1000L;

        CombatManager.cancelNewbieProt(newbie);
        this.expireTask = Bukkit.getScheduler().runTaskLater(ServerCore.getInstance(), () -> {
            CombatManager.removeNewbieProt(Bukkit.getOfflinePlayer(uuid));
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) CoreUtils.sendIfNotBlank(player, Messages.NEWBIE_PROT_EXPIRED);
        }, 20L * duration);
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void cancelExpireTask() {
        if (cancelled) return;
        expireTask.cancel();
        cancelled = true;
    }
}
