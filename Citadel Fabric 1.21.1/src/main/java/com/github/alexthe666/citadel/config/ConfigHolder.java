package com.github.alexthe666.citadel.config;

public class ConfigHolder {

    public static ServerConfig SERVER;

    public static void loadConfig() {
        SERVER = new ServerConfig();
    }
}
