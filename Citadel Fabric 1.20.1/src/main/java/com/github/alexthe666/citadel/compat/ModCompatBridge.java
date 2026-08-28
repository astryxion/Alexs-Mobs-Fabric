package com.github.alexthe666.citadel.compat;

import com.github.alexthe666.citadel.Citadel;
import net.fabricmc.loader.api.FabricLoader;

public class ModCompatBridge {

    private static boolean terrablender;

    /*
        Must be executed after all mods that require citadel have been loaded
     */
    public static void afterAllModsLoaded(){
        if (FabricLoader.getInstance().isModLoaded("terrablender")) {
            Citadel.LOGGER.info("adding citadel surface rules via terrablender...");
            com.github.alexthe666.citadel.compat.terrablender.TerrablenderCompat.setup();
            terrablender = true;
        }
    }

    public static boolean usingTerrablender(){
        return terrablender;
    }
}
