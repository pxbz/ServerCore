package me.pxbz.servercore;

import me.pxbz.servercore.utils.ConfigUtils;

public final class Permissions {
    private Permissions() {}

    public static String SEE_VANISHED_PLAYERS;
    public static String USE_STAFF_CHAT;
    public static String USE_PRIVATE_CHAT;
    public static String COLOURED_CHAT_PERMISSION;

    static {
        reloadPermissions();
    }

    public static void reloadPermissions() {
        SEE_VANISHED_PLAYERS = ConfigUtils.getStr("modules.essentials.see-vanished-players-permission");
        USE_STAFF_CHAT = ConfigUtils.getStr("modules.chat.commands.staffchat.permission");
        USE_PRIVATE_CHAT = ConfigUtils.getStr("modules.chat.commands.privatechat.permission");
        COLOURED_CHAT_PERMISSION = ConfigUtils.getStr("modules.chat.coloured-chat-permission");
    }
}
