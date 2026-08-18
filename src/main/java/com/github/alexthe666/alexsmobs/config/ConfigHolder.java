package com.github.alexthe666.alexsmobs.config;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigHolder {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String COMMON_CONFIG_FILE = "alexsmobs-common.json";

    public static final CommonConfig COMMON = loadCommonConfig();

    private ConfigHolder() {
    }

    private static CommonConfig loadCommonConfig() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(COMMON_CONFIG_FILE);
        CommonConfig defaults = new CommonConfig();

        try {
            Files.createDirectories(configPath.getParent());

            if (!Files.exists(configPath)) {
                writeCommonConfig(configPath, defaults);
                return defaults;
            }

            try (Reader reader = Files.newBufferedReader(configPath)) {
                CommonConfig loaded = GSON.fromJson(reader, CommonConfig.class);
                if (loaded != null) {
                    return loaded;
                }
            }

            AlexsMobs.LOGGER.warn("Alex's Mobs config file {} was empty, rewriting defaults.", configPath);
            writeCommonConfig(configPath, defaults);
        } catch (Exception e) {
            AlexsMobs.LOGGER.warn("Could not load Alex's Mobs config file, using defaults.", e);
        }

        return defaults;
    }

    private static void writeCommonConfig(Path configPath, CommonConfig config) throws IOException {
        try (Writer writer = Files.newBufferedWriter(configPath)) {
            GSON.toJson(config, writer);
        }
    }
}