package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import com.mojang.serialization.MapCodec;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Fabric: 1:1 replacement for Forge global loot modifiers.
 * Registers custom LootItemConditions and uses LootTableEvents.MODIFY to add pools
 * that apply the same logic as the Forge modifiers (banana/blossom from leaves, ancient dart/pigshoes in chests).
 */
public class AMLootRegistry {

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, path);
    }

    public static LootItemConditionType BANANA_CONDITION_TYPE;
    public static LootItemConditionType BLOSSOM_CONDITION_TYPE;
    public static LootItemConditionType ANCIENT_DART_CONDITION_TYPE;
    public static LootItemConditionType PIGSHOES_CONDITION_TYPE;

    /** Loot table IDs for leaf blocks (banana drop). Mirrors data/alexsmobs/loot_modifiers/banana_drop.json. */
    private static final Set<ResourceLocation> BANANA_LOOT_TABLES = new HashSet<>(Arrays.asList(
            ResourceLocation.fromNamespaceAndPath("minecraft", "blocks/jungle_leaves")
    ));

    /** Loot table IDs for acacia leaves only (blossom drop). */
    private static final Set<ResourceLocation> ACACIA_LEAVES_LOOT_TABLES = new HashSet<>(Arrays.asList(
            ResourceLocation.fromNamespaceAndPath("minecraft", "blocks/acacia_leaves")
    ));

    /** Loot table IDs for ancient dart. Mirrors data/alexsmobs/loot_modifiers/ancient_dart.json. */
    private static final Set<ResourceLocation> ANCIENT_DART_LOOT_TABLES = new HashSet<>(Arrays.asList(
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/jungle_temple"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/jungle_temple_dispenser")
    ));

    /** Loot table IDs for pigshoes. Mirrors data/alexsmobs/loot_modifiers/pigshoes.json. */
    private static final Set<ResourceLocation> PIGSHOES_LOOT_TABLES = new HashSet<>(Arrays.asList(
            ResourceLocation.fromNamespaceAndPath("minecraft", "gameplay/piglin_bartering")
    ));

    /** Custom conditions that delegate to modifier shouldAdd(); types set in init(). */
    public static final class BananaCondition implements LootItemCondition {
        @Override
        public boolean test(LootContext context) {
            return BananaLootModifier.shouldAdd(context);
        }
        @Override
        public LootItemConditionType getType() { return BANANA_CONDITION_TYPE; }
    }
    public static final class BlossomCondition implements LootItemCondition {
        @Override
        public boolean test(LootContext context) {
            return BlossomLootModifier.shouldAdd(context);
        }
        @Override
        public LootItemConditionType getType() { return BLOSSOM_CONDITION_TYPE; }
    }
    public static final class AncientDartCondition implements LootItemCondition {
        @Override
        public boolean test(LootContext context) {
            return AncientDartLootModifier.shouldAdd(context);
        }
        @Override
        public LootItemConditionType getType() { return ANCIENT_DART_CONDITION_TYPE; }
    }
    public static final class PigshoesCondition implements LootItemCondition {
        @Override
        public boolean test(LootContext context) {
            return PigshoesLootModifier.shouldAdd(context);
        }
        @Override
        public LootItemConditionType getType() { return PIGSHOES_CONDITION_TYPE; }
    }

    private static boolean initialized;

    /** 1.21.1: LootItemConditionType uses MapCodec, not Serializer. No JSON data; 1:1 with Forge. Call after game bootstrap. */
    @SuppressWarnings("unchecked")
    public static void init() {
        if (initialized) return;
        try {
            Registry<LootItemConditionType> conditionTypeRegistry = (Registry<LootItemConditionType>) BuiltInRegistries.REGISTRY.getOrThrow((ResourceKey) Registries.LOOT_CONDITION_TYPE);
            BANANA_CONDITION_TYPE = Registry.register(conditionTypeRegistry, id("banana_drop"), new LootItemConditionType(MapCodec.unit(BananaCondition::new)));
            BLOSSOM_CONDITION_TYPE = Registry.register(conditionTypeRegistry, id("blossom_drop"), new LootItemConditionType(MapCodec.unit(BlossomCondition::new)));
            ANCIENT_DART_CONDITION_TYPE = Registry.register(conditionTypeRegistry, id("ancient_dart"), new LootItemConditionType(MapCodec.unit(AncientDartCondition::new)));
            PIGSHOES_CONDITION_TYPE = Registry.register(conditionTypeRegistry, id("pigshoes"), new LootItemConditionType(MapCodec.unit(PigshoesCondition::new)));
        } catch (IllegalStateException e) {
            AlexsMobs.LOGGER.warn("Loot condition type registry not available yet (1.21.1): {}", e.getMessage());
            initialized = true;
            return;
        }

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (!source.isBuiltin()) return;
            ResourceLocation tableId = key.location();

            if (BANANA_LOOT_TABLES.contains(tableId)) {
                tableBuilder.pool(LootPool.lootPool()
                        .when(BananaCondition::new)
                        .add(LootItem.lootTableItem(AMItemRegistry.BANANA))
                        .build());
            }
            if (ACACIA_LEAVES_LOOT_TABLES.contains(tableId)) {
                tableBuilder.pool(LootPool.lootPool()
                        .when(BlossomCondition::new)
                        .add(LootItem.lootTableItem(AMItemRegistry.ACACIA_BLOSSOM))
                        .build());
            }
            if (ANCIENT_DART_LOOT_TABLES.contains(tableId)) {
                tableBuilder.pool(LootPool.lootPool()
                        .when(AncientDartCondition::new)
                        .add(LootItem.lootTableItem(AMItemRegistry.ANCIENT_DART))
                        .build());
            }
            if (PIGSHOES_LOOT_TABLES.contains(tableId)) {
                tableBuilder.pool(LootPool.lootPool()
                        .when(PigshoesCondition::new)
                        .add(LootItem.lootTableItem(AMItemRegistry.PIGSHOES))
                        .build());
            }
        });
        initialized = true;
    }
}
