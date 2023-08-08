package me.pxbz.servercore.modules;

public enum CommandTriState {
    SUCCESS(true),
    FAILURE(false),
    SEND_USAGE(false);

    private final boolean booleanValue;

    CommandTriState(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public boolean value() {
        return this.booleanValue;
    }
}
