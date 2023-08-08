package me.pxbz.servercore.utils;

import com.mojang.authlib.GameProfile;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public final class NMSUtil {

    private NMSUtil() {}

    public static boolean setName(Player p, String name) {
        CraftPlayer craftP = (CraftPlayer) p;
        GameProfile profile = craftP.getProfile();

        try {
            Field oldName = profile.getClass().getDeclaredField("name");
            oldName.setAccessible(true);
            oldName.set(profile, name);
            return true;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }
}
