package com.github.alexthe666.alexsmobs.config;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.fabricmc.loader.api.FabricLoader;

public final class ConfigHolder {

    /** Fabric: load config from CommonConfig POJO and bake into AMConfig (1:1 with Forge behavior). */
    public static void loadConfig() {
        CommonConfig config = new CommonConfig();
        AMConfig.bake(config);
        applyBiomeModCompatOverrides();
    }

    /**
     * Mungus transformation type 2 rewrites chunk biome palettes and syncs clients via packets.
     * That fights worldgen biome replacers (Biome Replacer, TerraBlender, etc.) and can freeze generation/saves.
     */
    private static void applyBiomeModCompatOverrides() {
        if (AMConfig.mungusBiomeTransformationType != 2) {
            return;
        }
        boolean biomeReplacer = FabricLoader.getInstance().isModLoaded("biome_replacer");
        boolean terraBlender = FabricLoader.getInstance().isModLoaded("terrablender");
        if (biomeReplacer || terraBlender) {
            AMConfig.mungusBiomeTransformationType = 1;
            AlexsMobs.LOGGER.warn(
                    "Alex's Mobs: mungusBiomeTransformationType was 2 (chunk biome + packet sync) but {} is loaded. "
                            + "Forced to 1 (blocks only) to avoid worldgen freezes and save hangs. "
                            + "Set 0 in config to disable Mungus biome changes entirely.",
                    biomeReplacer ? "Biome Replacer" : "TerraBlender"
            );
        }
    }
}
