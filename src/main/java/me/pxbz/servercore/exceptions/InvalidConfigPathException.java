package me.pxbz.servercore.exceptions;

import org.bukkit.configuration.InvalidConfigurationException;

public class InvalidConfigPathException extends InvalidConfigurationException {

    public InvalidConfigPathException(String path) {
        super(path + " is not a valid path in the configuration file", new NullPointerException());
    }
}
