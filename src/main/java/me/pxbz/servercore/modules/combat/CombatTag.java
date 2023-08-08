package me.pxbz.servercore.modules.combat;

import me.pxbz.servercore.ServerCore;
import me.pxbz.servercore.Messages;
import me.pxbz.servercore.utils.CoreUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public final class CombatTag {

    private static final HashMap<UUID, CombatTag> combatTagMap = new HashMap<>();

    private final UUID player;
    private final UUID attacker;
    private final int duration;
    private final BukkitTask expireTask;
    private boolean cancelled = false;

    public CombatTag(Player player, Player attacker, int duration) {
        this.player = player.getUniqueId();
        this.attacker = attacker.getUniqueId();
        this.duration = duration < 1? 10 : duration;

        if (combatTagMap.containsKey(this.player)) combatTagMap.get(this.player).cancel();
        combatTagMap.put(this.player, this);
        this.expireTask = Bukkit.getScheduler().runTaskTimer(ServerCore.getInstance(), new Runnable() {
            int elapsed = 0;
            Player victim;
            @Override
            public void run() {
                victim = Bukkit.getPlayer(CombatTag.this.player);

                if (elapsed >= duration) {
                    cancel();
                    CoreUtils.sendActionBarIfNotBlank(victim, Messages.COMBAT_TAG_END
                            .replaceAll("\\{ATTACKER}", Bukkit.getOfflinePlayer(CombatTag.this.attacker).getName()));
                    CoreUtils.sendIfNotBlank(victim, Messages.COMBAT_TAG_END
                            .replaceAll("\\{ATTACKER}", Bukkit.getOfflinePlayer(CombatTag.this.attacker).getName()));
                    return;
                }

                CoreUtils.sendActionBarIfNotBlank(victim, Messages.COMBAT_TAG_ACTIONBAR
                        .replaceAll("\\{ATTACKER}", Bukkit.getOfflinePlayer(CombatTag.this.attacker).getName())
                        .replaceAll("\\{TIMELEFT}", duration - elapsed + ""));
                elapsed++;
            }
        }, 0L,20L);
    }

    private void cancel() {
        if (cancelled) return;
        this.expireTask.cancel();
        cancelled = true;
        combatTagMap.remove(player);
    }

    public static boolean isCombatTagged(Player p) {
        return combatTagMap.containsKey(p.getUniqueId());
    }

    public static void combatLog(Player p) {
        if (!isCombatTagged(p)) return;

        CombatTag combatTag = combatTagMap.get(p.getUniqueId());
        combatTag.cancel();
        CoreUtils.broadcastIfNotBlank(Messages.COMBAT_LOGGED
                .replaceAll("\\{VICTIM}", p.getName())
                .replaceAll("\\{ATTACKER}", Bukkit.getOfflinePlayer(combatTag.attacker).getName()));
        p.setHealth(0);
    }

    public static void uncombatTag(Player p) {
        if (!isCombatTagged(p)) return;

        CombatTag combatTag = combatTagMap.get(p.getUniqueId());
        combatTag.cancel();
        CoreUtils.sendIfNotBlank(p, Messages.COMBAT_TAG_END
                .replaceAll("\\{ATTACKER}", Bukkit.getOfflinePlayer(combatTag.attacker).getName()));
    }
}
