package me.pxbz.servercore.modules.chat.listeners;

import me.pxbz.servercore.Permissions;
import me.pxbz.servercore.utils.ConfigUtils;
import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.Messages;
import me.pxbz.servercore.modules.chat.ChatManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChatFormatListener implements Listener {
    private String format = CoreUtils.formatServerInfo(ConfigUtils.getStr("modules.chat.format"));
    private String staffChatformat = CoreUtils.formatServerInfo(ConfigUtils.getStr("modules.chat.staff-chat-format"));
    private String privateChatformat = CoreUtils.formatServerInfo(ConfigUtils.getStr("modules.chat.private-chat-format"));

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (ChatManager.inStaffChat(e.getPlayer())) {
            e.getRecipients().removeIf(p -> !p.hasPermission(Permissions.USE_STAFF_CHAT) && !ChatManager.inStaffChat(p));
            e.setFormat(ChatManager.formatChatFormat(staffChatformat, e.getPlayer(), e.getMessage()));
        }
        else if (ChatManager.inPrivateChat(e.getPlayer())) {
            e.getRecipients().removeIf(p -> !p.hasPermission(Permissions.USE_PRIVATE_CHAT) && !ChatManager.inPrivateChat(p));
            e.setFormat(ChatManager.formatChatFormat(privateChatformat, e.getPlayer(), e.getMessage()));
        }
        else e.setFormat(ChatManager.formatChatFormat(format, e.getPlayer(), e.getMessage()));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (p.hasPermission(Permissions.USE_STAFF_CHAT)) {
            if (ChatManager.getChat(p) == ChatManager.ChatType.STAFF) {
                CoreUtils.sendIfNotBlank(p, Messages.STAFF_CHAT_ON);
                return;
            }
        }
        else if (p.hasPermission(Permissions.USE_PRIVATE_CHAT)) {
            if (ChatManager.getChat(p) == ChatManager.ChatType.PRIVATE) {
                CoreUtils.sendIfNotBlank(p, Messages.PRIVATE_CHAT_ON);
                return;
            }
        }
        else return;

        if (ChatManager.getChat(p) == ChatManager.ChatType.NORMAL) CoreUtils.sendIfNotBlank(p, Messages.PUBLIC_CHAT);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (ChatManager.inStaffChat(p) && !p.hasPermission(Permissions.USE_STAFF_CHAT))
            ChatManager.toggleStaffChat(p);
        else if (ChatManager.inPrivateChat(p) && !p.hasPermission(Permissions.USE_PRIVATE_CHAT))
            ChatManager.togglePrivateChat(p);
    }
}
