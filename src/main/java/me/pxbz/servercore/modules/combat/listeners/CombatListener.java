package me.pxbz.servercore.modules.combat.listeners;

import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.combat.CombatManager;
import me.pxbz.servercore.modules.combat.CombatTag;
import me.pxbz.servercore.utils.ConfigUtils;
import me.pxbz.servercore.utils.CoreUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CombatListener implements Listener {
    private int combatTagDuration = ConfigUtils.getInt("modules.combat.combat-tag-duration", 0);

    @EventHandler(priority =  EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntityType() != EntityType.PLAYER && e.getDamager().getType() != EntityType.PLAYER) return;
        if (e.isCancelled() || e.getEntity() == e.getDamager()) return;

        Player victim = (Player) e.getEntity();
        Player attacker = (Player) e.getDamager();

        // Newbie Protection
        if (CombatManager.hasNewbieProt(victim)) {
            e.setCancelled(true);
            CoreUtils.sendIfNotBlank(attacker, Messages.VICTIM_HAS_NEWBIE_PROT
                    .replaceAll("\\{TARGET}", victim.getName()));
        }
        else if (CombatManager.hasNewbieProt(attacker)) {
            e.setCancelled(true);
            CoreUtils.sendIfNotBlank(attacker, Messages.ATTACKER_HAS_NEWBIE_PROT
                    .replaceAll("\\{TARGET}", victim.getName()));
        }

        // Combat tagging

        else {
            if (combatTagDuration < 1) return;

            if (!CombatTag.isCombatTagged(victim)) CoreUtils.sendIfNotBlank(victim, Messages.COMBAT_TAGGED_VICTIM
                    .replaceAll("\\{ATTACKER}", attacker.getName()));
            if (!CombatTag.isCombatTagged(attacker)) CoreUtils.sendIfNotBlank(attacker, Messages.COMBAT_TAGGED_ATTACKER
                    .replaceAll("\\{VICTIM}", victim.getName()));

            new CombatTag(victim, attacker, combatTagDuration);
            new CombatTag(attacker, victim, combatTagDuration);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        CombatTag.combatLog(e.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        CombatTag.uncombatTag(e.getEntity());
    }
}
