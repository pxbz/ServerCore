package me.pxbz.servercore.utils;

import me.pxbz.servercore.ServerCore;
import me.pxbz.servercore.exceptions.InvalidConfigPathException;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ConfigUtils {
    public static FileConfiguration config = ServerCore.getInstance().getConfig();

    private ConfigUtils() {}

    public static void reloadConfig() {
        config = ServerCore.getInstance().getConfig();
    }

    public static String getStr(@NotNull String path) {
        return config.getString(path);
    }

    public static String getStr(@NotNull String path, String def) {
        return config.getString(path, def);
    }

    public static String getNonNullStr(@NotNull String path) throws InvalidConfigPathException {
        String result = getStr(path);
        if (result == null) throw new InvalidConfigPathException(path);
        return result;
    }

    public static int getInt(@NotNull String path) {
        return config.getInt(path);
    }

    public static int getInt(@NotNull String path, int def) {
        return config.getInt(path, def);
    }

    public static boolean getBoolean(@NotNull String path) {
        return config.getBoolean(path);
    }

    public static boolean getBoolean(@NotNull String path, boolean def) {
        return config.getBoolean(path, def);
    }

    public static String getMessage(@NotNull String path) {
        String s = config.getString("messages." + path);
        return s == null? null : CoreUtils.colour(CoreUtils.formatServerInfo(s));
    }

    public static List<String> getStrList(@NotNull String path) {
        return config.getStringList(path);
    }
}
