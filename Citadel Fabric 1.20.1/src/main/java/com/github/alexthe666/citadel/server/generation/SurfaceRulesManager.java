package com.github.alexthe666.citadel.server.generation;

import com.github.alexthe666.citadel.Citadel;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.levelgen.SurfaceRules;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SurfaceRulesManager {
    private static final List<SurfaceRules.RuleSource> OVERWORLD_REGISTRY = new ArrayList();
    private static final List<SurfaceRules.RuleSource> NETHER_REGISTRY = new ArrayList();
    private static final List<SurfaceRules.RuleSource> END_REGISTRY = new ArrayList();
    private static final List<SurfaceRules.RuleSource> CAVE_REGISTRY = new ArrayList();

    public SurfaceRulesManager() {
    }

    public static void registerOverworldSurfaceRule(SurfaceRules.ConditionSource condition, SurfaceRules.RuleSource rule) {
        registerOverworldSurfaceRule(SurfaceRules.ifTrue(condition, rule));
    }

    public static void registerOverworldSurfaceRule(SurfaceRules.RuleSource rule) {
        OVERWORLD_REGISTRY.add(rule);
    }

    @Deprecated
    public static void registerNetherSurfaceRule(SurfaceRules.ConditionSource condition, SurfaceRules.RuleSource rule) {
        registerNetherSurfaceRule(SurfaceRules.ifTrue(condition, rule));
    }

    @Deprecated
    public static void registerNetherSurfaceRule(SurfaceRules.RuleSource rule) {
        NETHER_REGISTRY.add(rule);
    }

    @Deprecated
    public static void registerEndSurfaceRule(SurfaceRules.ConditionSource condition, SurfaceRules.RuleSource rule) {
        registerEndSurfaceRule(SurfaceRules.ifTrue(condition, rule));
    }

    @Deprecated
    public static void registerEndSurfaceRule(SurfaceRules.RuleSource rule) {
        END_REGISTRY.add(rule);
    }

    @Deprecated
    public static void registerCaveSurfaceRule(SurfaceRules.ConditionSource condition, SurfaceRules.RuleSource rule) {
        registerCaveSurfaceRule(SurfaceRules.ifTrue(condition, rule));
    }

    @Deprecated
    public static void registerCaveSurfaceRule(SurfaceRules.RuleSource rule) {
        CAVE_REGISTRY.add(rule);
    }

    public static boolean hasOverworldModifications(){
        return !OVERWORLD_REGISTRY.isEmpty();
    }

    public static SurfaceRules.RuleSource mergeOverworldRules(SurfaceRules.RuleSource rulesIn) {
        Citadel.LOGGER.info("merged {} surface rules with vanilla rule {}", OVERWORLD_REGISTRY.size(), rulesIn.getClass().getSimpleName());
        return mergeRules(rulesIn, SurfaceRules.sequence(OVERWORLD_REGISTRY.toArray(SurfaceRules.RuleSource[]::new)));
    }

    /*
        Needed for terrablender compatibility. Uses reflection to avoid referencing private BiomeConditionSource at compile time.
     */
    @SuppressWarnings("unchecked")
    private static Object getBiomesFromBiomeConditionSource(Object biomeRule) {
        try {
            Class<?> cls = Class.forName("net.minecraft.world.level.levelgen.SurfaceRules$BiomeConditionSource");
            java.lang.reflect.Field f = cls.getDeclaredField("biomes");
            f.setAccessible(true);
            return f.get(biomeRule);
        } catch (Exception e) {
            return null;
        }
    }

    public static Map<String, SurfaceRules.RuleSource> getOverworldRulesByBiomeForTerrablender(boolean vanilla) {
        Map<String, SurfaceRules.RuleSource> map = new HashMap<>();
        for (SurfaceRules.RuleSource ruleSource : OVERWORLD_REGISTRY) {
            if (ruleSource instanceof SurfaceRules.TestRuleSource testRuleSource && testRuleSource.ifTrue().getClass().getName().equals("net.minecraft.world.level.levelgen.SurfaceRules$BiomeConditionSource")) {
                Object biomeRule = testRuleSource.ifTrue();
                Object biomes = getBiomesFromBiomeConditionSource(biomeRule);
                if (biomes == null) continue;
                boolean empty = biomes instanceof java.util.Collection && ((java.util.Collection<?>) biomes).isEmpty();
                if (biomes instanceof java.lang.Iterable) {
                    if (!((java.lang.Iterable<?>) biomes).iterator().hasNext()) {
                        empty = true;
                    } else {
                        empty = false;
                    }
                } else if (biomes instanceof java.util.Collection) {
                    empty = ((java.util.Collection<?>) biomes).isEmpty();
                }
                if (empty) continue;
                Object first = null;
                if (biomes instanceof java.util.List) {
                    first = ((java.util.List<?>) biomes).get(0);
                } else if (biomes instanceof java.lang.Iterable) {
                    first = java.util.stream.StreamSupport.stream(((java.lang.Iterable<?>) biomes).spliterator(), false).findFirst().orElse(null);
                }
                if (first == null) continue;
                String namespace = "minecraft";
                if (first instanceof net.minecraft.core.Holder<?> h && h.unwrapKey().isPresent()) {
                    namespace = h.unwrapKey().get().location().getNamespace();
                }
                boolean vanillaBiome = namespace.equals("minecraft");

                if (vanilla && vanillaBiome) {
                    map.put(namespace, testRuleSource);
                }
                if (!vanilla && !vanillaBiome) {
                    if (map.containsKey(namespace)) {
                        SurfaceRules.RuleSource ruleSource1 = map.get(namespace);
                        if (ruleSource1 instanceof SurfaceRules.SequenceRuleSource sequenceRuleSource) {
                            ImmutableList.Builder<SurfaceRules.RuleSource> ruleSources = ImmutableList.builder();
                            ruleSources.addAll(sequenceRuleSource.sequence());
                            ruleSources.add(testRuleSource);
                            map.put(namespace, SurfaceRules.sequence(ruleSources.build().toArray(SurfaceRules.RuleSource[]::new)));
                        } else {
                            map.put(namespace, SurfaceRules.sequence(ruleSource1, testRuleSource));
                        }
                    } else {
                        map.put(namespace, testRuleSource);
                    }
                }
            }
        }
        return map;
    }


    private static SurfaceRules.RuleSource mergeRules(SurfaceRules.RuleSource prev, SurfaceRules.RuleSource toMerge) {
        CitadelSurfaceRuleWrapper result;
        if (prev instanceof CitadelSurfaceRuleWrapper wrapper) {
            result = new CitadelSurfaceRuleWrapper(wrapper.vanillaRules(), toMerge);
        } else {
            result = new CitadelSurfaceRuleWrapper(prev, toMerge);
        }
        Citadel.LOGGER.debug("surface rule recursive depth: {}", calculateSurfaceRuleDepth(result, 1));
        return result;
    }

    private static int calculateSurfaceRuleDepth(SurfaceRules.RuleSource source, int depthIn) {
        if (source instanceof SurfaceRules.SequenceRuleSource sequenceRuleSource) {
            int j = depthIn;
            for (SurfaceRules.RuleSource ruleSource : sequenceRuleSource.sequence()) {
                j = Math.max(calculateSurfaceRuleDepth(ruleSource, depthIn + 1), j);
            }
            return j;
        } else if (source instanceof SurfaceRules.TestRuleSource testRuleSource) {
            depthIn = Math.max(calculateSurfaceRuleDepth(testRuleSource.thenRun(), depthIn + 1), depthIn);
        } else if (source instanceof CitadelSurfaceRuleWrapper citadelSurfaceRuleWrapper) {
            depthIn = Math.max(calculateSurfaceRuleDepth(citadelSurfaceRuleWrapper.vanillaRules(), depthIn + 1), depthIn);
        }
        return depthIn;
    }
}