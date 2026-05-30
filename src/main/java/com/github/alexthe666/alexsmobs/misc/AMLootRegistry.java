package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.Serializer;

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
        return new ResourceLocation(AlexsMobs.MODID, path);
    }

    public static LootItemConditionType BANANA_CONDITION_TYPE;
    public static LootItemConditionType BLOSSOM_CONDITION_TYPE;
    public static LootItemConditionType ANCIENT_DART_CONDITION_TYPE;
    public static LootItemConditionType PIGSHOES_CONDITION_TYPE;

    /** Loot table IDs for leaf blocks (banana drop). Mirrors data/alexsmobs/loot_modifiers/banana_drop.json. */
    private static final Set<ResourceLocation> BANANA_LOOT_TABLES = new HashSet<>(Arrays.asList(
            new ResourceLocation("minecraft", "blocks/jungle_leaves")
    ));

    /** Loot table IDs for acacia leaves only (blossom drop). */
    private static final Set<ResourceLocation> ACACIA_LEAVES_LOOT_TABLES = new HashSet<>(Arrays.asList(
            new ResourceLocation("minecraft", "blocks/acacia_leaves")
    ));

    /** Loot table IDs for ancient dart. Mirrors data/alexsmobs/loot_modifiers/ancient_dart.json. */
    private static final Set<ResourceLocation> ANCIENT_DART_LOOT_TABLES = new HashSet<>(Arrays.asList(
            new ResourceLocation("minecraft", "chests/jungle_temple"),
            new ResourceLocation("minecraft", "chests/jungle_temple_dispenser")
    ));

    /** Loot table IDs for pigshoes. Mirrors data/alexsmobs/loot_modifiers/pigshoes.json. */
    private static final Set<ResourceLocation> PIGSHOES_LOOT_TABLES = new HashSet<>(Arrays.asList(
            new ResourceLocation("minecraft", "gameplay/piglin_bartering")
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

    /** Serializers for custom conditions (no JSON data; 1:1 with Forge). */
    private static final Serializer<BananaCondition> BANANA_SERIALIZER = new Serializer<>() {
        @Override
        public void serialize(JsonObject json, BananaCondition condition, JsonSerializationContext context) {}
        @Override
        public BananaCondition deserialize(JsonObject json, JsonDeserializationContext context) { return new BananaCondition(); }
    };
    private static final Serializer<BlossomCondition> BLOSSOM_SERIALIZER = new Serializer<>() {
        @Override
        public void serialize(JsonObject json, BlossomCondition condition, JsonSerializationContext context) {}
        @Override
        public BlossomCondition deserialize(JsonObject json, JsonDeserializationContext context) { return new BlossomCondition(); }
    };
    private static final Serializer<AncientDartCondition> ANCIENT_DART_SERIALIZER = new Serializer<>() {
        @Override
        public void serialize(JsonObject json, AncientDartCondition condition, JsonSerializationContext context) {}
        @Override
        public AncientDartCondition deserialize(JsonObject json, JsonDeserializationContext context) { return new AncientDartCondition(); }
    };
    private static final Serializer<PigshoesCondition> PIGSHOES_SERIALIZER = new Serializer<>() {
        @Override
        public void serialize(JsonObject json, PigshoesCondition condition, JsonSerializationContext context) {}
        @Override
        public PigshoesCondition deserialize(JsonObject json, JsonDeserializationContext context) { return new PigshoesCondition(); }
    };

    public static void init() {
        BANANA_CONDITION_TYPE = Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, id("banana_drop"), new LootItemConditionType(BANANA_SERIALIZER));
        BLOSSOM_CONDITION_TYPE = Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, id("blossom_drop"), new LootItemConditionType(BLOSSOM_SERIALIZER));
        ANCIENT_DART_CONDITION_TYPE = Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, id("ancient_dart"), new LootItemConditionType(ANCIENT_DART_SERIALIZER));
        PIGSHOES_CONDITION_TYPE = Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, id("pigshoes"), new LootItemConditionType(PIGSHOES_SERIALIZER));

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (!source.isBuiltin()) return;
            ResourceLocation tableId = id;

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
    }
}
