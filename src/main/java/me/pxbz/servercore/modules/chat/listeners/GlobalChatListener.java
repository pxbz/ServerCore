package me.pxbz.servercore.modules.chat.listeners;

import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.ServerCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GlobalChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        sendGlobalChat(e.getFormat());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e) {
        sendGlobalChat(e.getJoinMessage());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent e) {
        sendGlobalChat(e.getQuitMessage());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent e) {
        sendGlobalChat(e.getDeathMessage());
    }

    public static void sendGlobalChat(String input) {
        if (input == null) return;
        ServerCore.getSubserver().sendGlobalChat(CoreUtils.uncolour(CoreUtils.formatServerInfo(
                "{C2}[&r" + CoreUtils.getColouredServerName() + "{C2}]&r ") + input));
    }
}
