package me.pxbz.servercore.modules.essentials;

import me.pxbz.servercore.ServerCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class EssentialsManager {
    private static final HashMap<UUID, ItemStack[]> clearedItems = new HashMap<>();
    public static final Set<UUID> vanishedPlayers = new HashSet<>();
//    public static final Set<UUID> superVanishedPlayers = new HashSet<>();

    public static String seeVanishedPlayersPermission = "modules.essentials.see-vanished-players-permission";
//    private static String seeSuperVanishedPlayersPermission = "";

    private EssentialsManager() {}

    public static boolean isVanished(OfflinePlayer p) {
        return vanishedPlayers.contains(p.getUniqueId());
    }

//    public static boolean isSuperVanished(OfflinePlayer p) {
//        return superVanishedPlayers.contains(p.getUniqueId());
//    }

    public static void vanish(Player p) {
        vanishedPlayers.add(p.getUniqueId());
        for (Player t : Bukkit.getOnlinePlayers()) {
            if (!t.hasPermission(seeVanishedPlayersPermission)) t.hidePlayer(ServerCore.getInstance(), p);
        }
    }

    public static void unvanish(Player p) {
        vanishedPlayers.remove(p.getUniqueId());
        Bukkit.getOnlinePlayers().forEach(t -> t.showPlayer(ServerCore.getInstance(), p));
    }

    public static void setClearedItems(Player p, ItemStack[] items) {
        boolean empty = true;
        for (ItemStack item : items) {
            if (item != null) {
                empty = false;
                break;
            }
        }

        if (empty) clearedItems.remove(p.getUniqueId());
        else clearedItems.put(p.getUniqueId(), items);
    }

    public static ItemStack[] getClearedItems(Player p) {
        return clearedItems.get(p.getUniqueId());
    }
}
