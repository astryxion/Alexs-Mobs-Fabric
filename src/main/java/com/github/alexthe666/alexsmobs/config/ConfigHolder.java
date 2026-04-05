package com.github.alexthe666.alexsmobs.config;

public final class ConfigHolder {

    /** Fabric: load config from CommonConfig POJO and bake into AMConfig (1:1 with Forge behavior). */
    public static void loadConfig() {
        CommonConfig config = new CommonConfig();
        AMConfig.bake(config);
    }
}