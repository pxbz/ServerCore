package me.pxbz.servercore;

import me.pxbz.servercore.network.Subserver;
import me.pxbz.servercore.utils.CoreUtils;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class ServerCore extends JavaPlugin {

    public static ServerCore instance;
    private static Subserver subserver;
    private static Chat chat;
    private static Permission perms;

    @Override
    public void onEnable() {
        instance = this;
        if (!getDataFolder().exists()) getDataFolder().mkdir();
        saveDefaultConfig();

        subserver = new Subserver(CoreUtils.getServerName(), 6969);
        subserver.start();

        Permissions.reloadPermissions();
        Messages.reloadMessages();
        setupChat();
        setupPermissions();
        writeConfigDefaults();
        CommandManager.registerAllCommands();
        EventManager.registerEvents();
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public static Chat getChat() {
        return ServerCore.chat;
    }

    public static Permission getPerms() {
        return ServerCore.perms;
    }

    public static ServerCore getInstance() {
        return instance;
    }

    public static Subserver getSubserver() {
        return subserver;
    }

    public static void writeConfigDefaults() {
        FileConfiguration config = instance.getConfig();
        ConfigurationSection messages = config.getConfigurationSection("messages");
        if (messages == null) messages = config.createSection("messages");

        messages.addDefault("command-invalid-player", "{PREFIX} &cPlayer '&4{PLAYER}&c' is invalid or not online.");
        messages.addDefault("teleporting-to-player", "{PREFIX} &bTeleporting to &e{TARGET}&b.");
        messages.addDefault("teleporting-player-to-player", "{PREFIX} &bTeleporting &e{TARGET1} &bto &e{TARGET2}&b.");
        messages.addDefault("teleporting-mass-to-player", "{PREFIX} &bTeleporting &e{TARGETS} &bto &e{TARGET}&b.");
        messages.addDefault("teleporting-player-to-sender", "{PREFIX} &bTeleporting &e{TARGET} &bto your location.");
        messages.addDefault("teleporting-mass-to-sender", "{PREFIX} &bTeleporting &e{TARGETS} &bto your location.");
        messages.addDefault("teleporting-all-to-sender", "{PREFIX} &bTeleporting all players to your location.");
    }
}

