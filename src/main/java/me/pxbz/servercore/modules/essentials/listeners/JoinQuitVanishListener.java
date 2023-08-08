package me.pxbz.servercore.modules.essentials.listeners;

import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.ServerCore;
import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.essentials.EssentialsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class JoinQuitVanishListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player ep = e.getPlayer();
        if (!ep.hasPermission(EssentialsManager.seeVanishedPlayersPermission)) {
            for (UUID uuid : EssentialsManager.vanishedPlayers) {
                Player p = Bukkit.getPlayer(uuid);
                if (p == null) continue;
                ep.hidePlayer(ServerCore.getInstance(), p);
            }
        }

        if (EssentialsManager.isVanished(ep)) {

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.hasPermission(EssentialsManager.seeVanishedPlayersPermission)) p.hidePlayer(ServerCore.getInstance(), ep);
                else p.sendMessage(CoreUtils.formatServerInfo("{C2}[{C1}VANISHED{C2}]&r " + e.getJoinMessage()));
            }
            e.setJoinMessage(null);
            CoreUtils.sendIfNotBlank(ep, Messages.VANISHED);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player ep = e.getPlayer();
        if (EssentialsManager.isVanished(ep)) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission(EssentialsManager.seeVanishedPlayersPermission))
                    p.sendMessage(CoreUtils.colour(CoreUtils.formatServerInfo("{C2}[{C1}VANISHED{C2}]&r " + e.getQuitMessage())));
            }
            e.setQuitMessage(null);
        }
    }
}
