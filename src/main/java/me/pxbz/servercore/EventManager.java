package me.pxbz.servercore;

import me.pxbz.servercore.modules.chat.listeners.ChatFormatListener;
import me.pxbz.servercore.modules.chat.listeners.GlobalChatListener;
import me.pxbz.servercore.modules.chat.listeners.MuteChatListener;
import me.pxbz.servercore.modules.combat.listeners.CombatListener;
import me.pxbz.servercore.modules.combat.listeners.FirstJoinListener;
import me.pxbz.servercore.modules.essentials.listeners.JoinQuitVanishListener;
import me.pxbz.servercore.modules.teleportation.listeners.TeleportListener;
import me.pxbz.servercore.modules.warping.listeners.SpawnOnJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.HashSet;

public final class EventManager {

    private static final ServerCore instance = ServerCore.getInstance();

    private static final HashSet<Listener> serverCoreEvents = new HashSet<>();

    private EventManager() {}

    public static void registerEvents() {
        serverCoreEvents.addAll(Arrays.asList(
                new ChatFormatListener(),
                new GlobalChatListener(),
                new MuteChatListener(),

                new FirstJoinListener(),
                new CombatListener(),

                new JoinQuitVanishListener(),

                new TeleportListener(),

                new SpawnOnJoinListener()
        ));

        serverCoreEvents.forEach(e -> Bukkit.getPluginManager().registerEvents(e, instance));

    }

    public static void unregisterEvents() {
        new HashSet<>(serverCoreEvents).forEach(e -> {
            HandlerList.unregisterAll(e);
            serverCoreEvents.remove(e);
        });
    }
}
