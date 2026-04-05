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

    /** Loot table IDs for leaf blocks (banana drop). 1:1 with Forge modifier targeting leaves. */
    private static final Set<ResourceLocation> LEAVES_LOOT_TABLES = new HashSet<>(Arrays.asList(
            ResourceLocation.fromNamespaceAndPath("minecraft", "blocks/oak_leaves"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "blocks/jungle_leaves"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "blocks/birch_leaves"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "blocks/spruce_leaves"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "blocks/acacia_leaves"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "blocks/dark_oak_leaves"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "blocks/mangrove_leaves"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "blocks/cherry_leaves"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "blocks/azalea_leaves"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "blocks/flowering_azalea_leaves")
    ));

    /** Loot table IDs for acacia leaves only (blossom drop). */
    private static final Set<ResourceLocation> ACACIA_LEAVES_LOOT_TABLES = new HashSet<>(Arrays.asList(
            ResourceLocation.fromNamespaceAndPath("minecraft", "blocks/acacia_leaves")
    ));

    /** Loot table IDs for chests (ancient dart and pigshoes). 1:1 with Forge modifier targeting chests. */
    private static final Set<ResourceLocation> CHEST_LOOT_TABLES = new HashSet<>(Arrays.asList(
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/abandoned_mineshaft"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/buried_treasure"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/desert_pyramid"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/end_city_treasure"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/jungle_temple"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/nether_bridge"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/shipwreck_supply"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/shipwreck_treasure"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/simple_dungeon"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/stronghold_corridor"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/stronghold_crossing"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/stronghold_library"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/underwater_ruin_small"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/underwater_ruin_big"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/woodland_mansion"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/village/village_weaponsmith"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/village/village_toolsmith"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/village/village_armorer"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/village/village_cartographer"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/village/village_mason"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/village/village_shepherd"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/village/village_butcher"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/village/village_fisher"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/village/village_fletcher"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/village/village_desert_house"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/village/village_plains_house"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/village/village_savanna_house"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/village/village_snowy_house"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/village/village_taiga_house"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/pillager_outpost"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/bastion_treasure"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/bastion_other"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/bastion_bridge"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/bastion_hoglin_stable"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "chests/ruined_portal")
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

            if (LEAVES_LOOT_TABLES.contains(tableId)) {
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
            if (CHEST_LOOT_TABLES.contains(tableId)) {
                tableBuilder.pool(LootPool.lootPool()
                        .when(AncientDartCondition::new)
                        .add(LootItem.lootTableItem(AMItemRegistry.ANCIENT_DART))
                        .build());
                tableBuilder.pool(LootPool.lootPool()
                        .when(PigshoesCondition::new)
                        .add(LootItem.lootTableItem(AMItemRegistry.PIGSHOES))
                        .build());
            }
        });
        initialized = true;
    }
}
