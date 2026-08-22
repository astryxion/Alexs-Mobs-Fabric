package com.github.alexthe666.citadel.compat.terrablender;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.lang.reflect.Method;
import java.util.Map;

public class TerrablenderCompat {

    public static void setup() {
        try {
            Class<?> surfaceRuleManagerClass = Class.forName("terrablender.api.SurfaceRuleManager");
            Class<?> ruleCategoryClass = Class.forName("terrablender.api.SurfaceRuleManager$RuleCategory");
            Class<?> ruleStageClass = Class.forName("terrablender.api.SurfaceRuleManager$RuleStage");

            Object overworld = getEnum(ruleCategoryClass, "OVERWORLD");
            Object beforeBedrock = getEnum(ruleStageClass, "BEFORE_BEDROCK");

            Map<String, SurfaceRules.RuleSource> vanillaBiomeRules = SurfaceRulesManager.getOverworldRulesByBiomeForTerrablender(true);
            Method addToDefault = surfaceRuleManagerClass.getMethod(
                    "addToDefaultSurfaceRulesAtStage",
                    ruleCategoryClass,
                    ruleStageClass,
                    int.class,
                    SurfaceRules.RuleSource.class
            );
            for (Map.Entry<String, SurfaceRules.RuleSource> entry : vanillaBiomeRules.entrySet()) {
                addToDefault.invoke(null, overworld, beforeBedrock, 0, entry.getValue());
            }
            Citadel.LOGGER.info("Added {} vanilla biome surface rule types via terrablender", vanillaBiomeRules.size());

            Map<String, SurfaceRules.RuleSource> moddedBiomeRules = SurfaceRulesManager.getOverworldRulesByBiomeForTerrablender(false);
            Method addSurfaceRules = surfaceRuleManagerClass.getMethod(
                    "addSurfaceRules",
                    ruleCategoryClass,
                    String.class,
                    SurfaceRules.RuleSource.class
            );
            for (Map.Entry<String, SurfaceRules.RuleSource> entry : moddedBiomeRules.entrySet()) {
                addSurfaceRules.invoke(null, overworld, entry.getKey(), entry.getValue());
            }
            Citadel.LOGGER.info("Added {} modded biome surface rule types via terrablender", moddedBiomeRules.size());
        } catch (ReflectiveOperationException e) {
            Citadel.LOGGER.error("Failed to setup Terrablender compat", e);
        }
    }

    private static <T extends Enum<T>> T getEnum(Class<?> enumClass, String constant) {
        @SuppressWarnings("unchecked")
        Class<T> typed = (Class<T>) enumClass;
        return Enum.valueOf(typed, constant);
    }
}
