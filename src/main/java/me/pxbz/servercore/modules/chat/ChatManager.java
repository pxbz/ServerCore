package me.pxbz.servercore.modules.chat;

import me.pxbz.servercore.Permissions;
import me.pxbz.servercore.utils.CoreUtils;
import me.pxbz.servercore.ServerCore;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class ChatManager {
    private static final Set<UUID> globalChat = new HashSet<>();
    public static boolean chatMuted = false;
    private static final Set<UUID> muteSpeakers = new HashSet<>();
    private static final HashMap<UUID, ChatType> chat = new HashMap<>();

    public enum ChatType {
        NORMAL,
        PRIVATE,
        STAFF
    }

    private ChatManager() {}

    public static Set<Player> getGlobalChatMembers() {
        Set<Player> globalChatMembers = new HashSet<>();
        for (UUID uuid : ChatManager.globalChat) {
            Player p = Bukkit.getPlayer(uuid);
            if (p == null) continue;
            globalChatMembers.add(p);
        }
        return globalChatMembers;
    }

    public static Set<UUID> getGlobalChat() {
        return ChatManager.globalChat;
    }

    public static void muteChat() {
        chatMuted = true;
    }

    public static void unmuteChat() {
        chatMuted = false;
        muteSpeakers.clear();
    }

    public static boolean isChatMuted() {
        return chatMuted;
    }

    public static void toggleMuteSpeaker(Player p) {
        UUID uuid = p.getUniqueId();
        if (muteSpeakers.contains(uuid)) muteSpeakers.remove(uuid);
        else muteSpeakers.add(uuid);
    }

    public static boolean isMuteSpeaker(Player p) {
        return muteSpeakers.contains(p.getUniqueId());
    }

    public static void toggleStaffChat(Player p) {
        if (inStaffChat(p)) chat.remove(p.getUniqueId());
        else chat.put(p.getUniqueId(), ChatType.STAFF);
    }

    public static boolean inStaffChat(Player p) {
        return chat.containsKey(p.getUniqueId()) && chat.get(p.getUniqueId()) == ChatType.STAFF;
    }

    public static void togglePrivateChat(Player p) {
        if (inPrivateChat(p)) chat.remove(p.getUniqueId());
        else chat.put(p.getUniqueId(), ChatType.PRIVATE);
    }

    public static boolean inPrivateChat(Player p) {
        return chat.containsKey(p.getUniqueId()) && chat.get(p.getUniqueId()) == ChatType.PRIVATE;
    }

    public static ChatType getChat(Player p) {
        ChatType playersChat = chat.get(p.getUniqueId());
        if (playersChat == null) return ChatType.NORMAL;
        else return playersChat;
    }

    public static String formatChatFormat(String format, Player sender, String msg) {
        String toReturn = CoreUtils.colour(format
                .replaceAll("\\{USERNAME}", sender.getName())
                .replaceAll("\\{PLAYERPREFIX}", ServerCore.getChat().getPlayerPrefix(sender))
                .replaceAll("\\{PLAYERSUFFIX}", ServerCore.getChat().getPlayerSuffix(sender)));
        msg = colourMessage(msg, sender);
        toReturn = toReturn.replaceAll("\\{MESSAGE}", msg);
        return toReturn;
    }

    public static String colourMessage(String msg, CommandSender sender) {
        if (sender instanceof Player) {
            String chatColour = ChatColourCommand.getColourPrefix((Player) sender);
            if (chatColour != null) msg = chatColour + msg;
            if (sender.hasPermission(Permissions.COLOURED_CHAT_PERMISSION)) msg = CoreUtils.colour(msg.replaceAll("&z", chatColour == null ? "&r" : chatColour));
        }

        return sender.hasPermission(Permissions.COLOURED_CHAT_PERMISSION)? CoreUtils.colour(msg) : msg;
    }
}
