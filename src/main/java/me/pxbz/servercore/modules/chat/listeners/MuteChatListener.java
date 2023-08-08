package me.pxbz.servercore.modules.chat.listeners;

import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.chat.ChatManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MuteChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (ChatManager.isChatMuted() && !ChatManager.isMuteSpeaker(p)) {
            e.setCancelled(true);
            CoreUtils.sendIfNotBlank(p, Messages.CANT_TALK_WHILE_CHAT_MUTED);
        }
    }
}
