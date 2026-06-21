package com.github.alexthe666.alexsmobs;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.config.BiomeConfig;
import com.github.alexthe666.alexsmobs.network.AMNetworking;
import net.fabricmc.api.ModInitializer;

/**
 * Fabric entrypoint for bootstrap that no longer runs on NeoForge lifecycle.
 * When {@link AlexsMobs} is fully ported to {@link ModInitializer}, merge into that class and remove this entrypoint from fabric.mod.json.
 */
public class AlexsMobsFabricBootstrap implements ModInitializer {

    @Override
    public void onInitialize() {
        AMNetworking.registerPayloadTypes();
        AMNetworking.registerServerReceivers();
        AMConfig.bake();
        BiomeConfig.init();
    }
}
