package com.github.alexthe666.citadel.server.generation;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;

import java.util.ArrayList;
import java.util.List;

public class SurfaceRulesManager {
    private static final List<MaterialRule> OVERWORLD_REGISTRY = new ArrayList<>();
    private static final List<MaterialRule> NETHER_REGISTRY = new ArrayList<>();
    private static final List<MaterialRule> END_REGISTRY = new ArrayList<>();
    private static final List<MaterialRule> CAVE_REGISTRY = new ArrayList<>();

    /**
     * Categories for surface rules to be classed under.
     * Used to conditionally apply rules only during actual world generation.
     */
    public enum RuleCategory {
        OVERWORLD, NETHER, END
    }

    public SurfaceRulesManager() {
    }

    public static void registerOverworldSurfaceRule(MaterialCondition condition, MaterialRule rule) {
        registerOverworldSurfaceRule(MaterialRules.ifTrue(condition, rule));
    }

    public static void registerOverworldSurfaceRule(MaterialRule rule) {
        OVERWORLD_REGISTRY.add(rule);
    }

    public static void registerNetherSurfaceRule(MaterialCondition condition, MaterialRule rule) {
        registerNetherSurfaceRule(MaterialRules.ifTrue(condition, rule));
    }

    public static void registerNetherSurfaceRule(MaterialRule rule) {
        NETHER_REGISTRY.add(rule);
    }

    public static void registerEndSurfaceRule(MaterialCondition condition, MaterialRule rule) {
        registerEndSurfaceRule(MaterialRules.ifTrue(condition, rule));
    }

    public static void registerEndSurfaceRule(MaterialRule rule) {
        END_REGISTRY.add(rule);
    }

    public static void registerCaveSurfaceRule(MaterialCondition condition, MaterialRule rule) {
        registerCaveSurfaceRule(MaterialRules.ifTrue(condition, rule));
    }

    public static void registerCaveSurfaceRule(MaterialRule rule) {
        CAVE_REGISTRY.add(rule);
    }

    /**
     * Merges Citadel surface rules with the existing (vanilla/other mods) rules.
     * Original rules are placed FIRST so that WorldWeaver, Blueprint, and vanilla
     * get priority; Citadel rules are appended and only apply where no prior rule matched
     * (e.g. Alex's Caves biomes). This avoids Citadel overwriting other mods' surface rules.
     */
    public static MaterialRule mergeRules(MaterialRule prev, List<MaterialRule> toMerge) {
        ImmutableList.Builder<MaterialRule> builder = ImmutableList.builder();
        builder.add(prev);
        builder.addAll(toMerge);
        return MaterialRules.sequence(builder.build().toArray(MaterialRule[]::new));
    }

    public static MaterialRule mergeOverworldRules(MaterialRule rulesIn) {
        return mergeRules(rulesIn, OVERWORLD_REGISTRY);
    }

    public static MaterialRule mergeNetherRules(MaterialRule rulesIn) {
        return mergeRules(rulesIn, NETHER_REGISTRY);
    }

    public static MaterialRule mergeEndRules(MaterialRule rulesIn) {
        return mergeRules(rulesIn, END_REGISTRY);
    }

    /**
     * Merge rules based on the given category.
     * @param category The rule category (dimension type)
     * @param rulesIn The original surface rules (vanilla/other mods)
     * @return Merged surface rules with original first, then Citadel rules appended
     */
    public static MaterialRule mergeRulesForCategory(RuleCategory category, MaterialRule rulesIn) {
        return switch (category) {
            case OVERWORLD -> mergeOverworldRules(rulesIn);
            case NETHER -> mergeNetherRules(rulesIn);
            case END -> mergeEndRules(rulesIn);
        };
    }

    /**
     * Check if there are any registered rules for the given category.
     */
    public static boolean hasRulesForCategory(RuleCategory category) {
        return switch (category) {
            case OVERWORLD -> !OVERWORLD_REGISTRY.isEmpty();
            case NETHER -> !NETHER_REGISTRY.isEmpty();
            case END -> !END_REGISTRY.isEmpty();
        };
    }
}
