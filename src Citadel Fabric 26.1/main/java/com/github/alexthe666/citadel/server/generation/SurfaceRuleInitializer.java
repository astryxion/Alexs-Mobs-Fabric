package com.github.alexthe666.citadel.server.generation;

import com.github.alexthe666.citadel.Citadel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

/**
 * Initializes surface rules for all dimensions on server start.
 * This must be called AFTER biome sources are initialized to avoid breaking biome lookup.
 */
public class SurfaceRuleInitializer {

    /**
     * Initialize surface rules for all level stems on server start.
     * Called from ServerAboutToStartEvent handler.
     */
    public static void initializeOnServerStart(MinecraftServer server) {
        Citadel.LOGGER.info("[Citadel] SurfaceRuleInitializer: Starting initialization...");
        Citadel.LOGGER.info("[Citadel] OVERWORLD rules registered: {}", SurfaceRulesManager.hasRulesForCategory(SurfaceRulesManager.RuleCategory.OVERWORLD));

        RegistryAccess registryAccess = server.registryAccess();
        registryAccess.lookupOrThrow(Registries.LEVEL_STEM).listElements().forEach(holder -> {
            ResourceKey<LevelStem> key = holder.key();
            LevelStem stem = holder.value();
            Citadel.LOGGER.info("[Citadel] Processing dimension: {}", key.identifier());
            initializeSurfaceRules(stem.type(), key, stem.generator());
        });
        Citadel.LOGGER.info("[Citadel] SurfaceRuleInitializer: Initialization complete.");
    }

    private static void initializeSurfaceRules(Holder<DimensionType> dimensionType,
                                               ResourceKey<LevelStem> levelResourceKey,
                                               ChunkGenerator chunkGenerator) {
        if (!(chunkGenerator instanceof NoiseBasedChunkGenerator noiseBasedChunkGenerator)) {
            return;
        }

        NoiseGeneratorSettings generatorSettings = noiseBasedChunkGenerator.generatorSettings().value();

        SurfaceRulesManager.RuleCategory ruleCategory = getRuleCategoryForDimension(dimensionType);

        if (ruleCategory == null) {
            return;
        }

        if (!SurfaceRulesManager.hasRulesForCategory(ruleCategory)) {
            return;
        }

        if (!((Object) generatorSettings instanceof IExtendedNoiseGeneratorSettings)) {
            Citadel.LOGGER.warn("NoiseGeneratorSettings mixin not applied, surface rules will not be injected for: {}",
                levelResourceKey.identifier());
            return;
        }

        ((IExtendedNoiseGeneratorSettings) (Object) generatorSettings).citadel$setRuleCategory(ruleCategory);

        Citadel.LOGGER.info("Initialized Citadel surface rules for dimension: {} (category: {})",
            levelResourceKey.identifier(), ruleCategory);
    }

    /**
     * Determine the rule category based on dimension type.
     */
    private static SurfaceRulesManager.RuleCategory getRuleCategoryForDimension(Holder<DimensionType> dimensionType) {
        if (dimensionType.is(BuiltinDimensionTypes.NETHER)) {
            return SurfaceRulesManager.RuleCategory.NETHER;
        }
        if (dimensionType.is(BuiltinDimensionTypes.END)) {
            return SurfaceRulesManager.RuleCategory.END;
        }
        if (dimensionType.is(BuiltinDimensionTypes.OVERWORLD)) {
            return SurfaceRulesManager.RuleCategory.OVERWORLD;
        }

        DimensionType type = dimensionType.value();

        if (type.hasCeiling()) {
            return SurfaceRulesManager.RuleCategory.NETHER;
        }

        if (!type.hasCeiling() && type.minY() == 0 && type.height() == 256) {
            return SurfaceRulesManager.RuleCategory.END;
        }

        return SurfaceRulesManager.RuleCategory.OVERWORLD;
    }
}
