package me.pxbz.servercore.modules.combat;

import me.pxbz.servercore.utils.ConfigUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public final class CombatManager {
    private static final HashMap<UUID, NewbieProtection> newbieProtectionMap = new HashMap<>();

    public static long getNewbieExpireTime(Player p) {
        UUID uuid = p.getUniqueId();
        return newbieProtectionMap.containsKey(uuid)? newbieProtectionMap.get(uuid).getExpireTime(): -1;
    }

    public static void setNewbieProtection(Player p) {
        UUID uuid = p.getUniqueId();
        int duration = ConfigUtils.getInt("modules.combat.newbie-prot-duration", 0);
        if (duration < 1) return;
        newbieProtectionMap.put(uuid, new NewbieProtection(p, duration));
    }

    public static boolean hasNewbieProt(Player p) {
        return newbieProtectionMap.containsKey(p.getUniqueId());
    }

    public static void removeNewbieProt(OfflinePlayer p) {
        newbieProtectionMap.remove(p.getUniqueId());
    }

    public static void cancelNewbieProt(OfflinePlayer p) {
        UUID uuid = p.getUniqueId();
        if (!newbieProtectionMap.containsKey(uuid)) return;

        newbieProtectionMap.get(uuid).cancelExpireTask();
        removeNewbieProt(p);
    }
}
