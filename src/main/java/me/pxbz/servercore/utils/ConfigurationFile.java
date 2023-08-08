package me.pxbz.servercore.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigurationFile {

    private final Logger logger;
    private final File file;
    private YamlConfiguration config;

    public ConfigurationFile(File file, JavaPlugin plugin) {
        this.logger = plugin.getLogger();
        this.file = file;

        if (!file.exists()) {
            try {
                if (!file.createNewFile()) logger.log(Level.SEVERE, "Failed to create file: " + file.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {config.save(file);} catch (IOException e) {e.printStackTrace();}
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void set(String path, Object obj) {
        config.set(path, obj);
        save();
        reload();
    }

    public YamlConfiguration getConfig() {
        return config;
    }
}
