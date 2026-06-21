package com.github.alexthe666.citadel.server.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

/**
 * Holds custom tool stats and builds a {@link ToolMaterial} (replaces removed {@code Tier}).
 */
public class CustomToolMaterial {
    private final String name;
    private final int harvestLevel;
    private final int durability;
    private final float damage;
    private final float speed;
    private final int enchantability;
    private Ingredient ingredient = null;
    private final TagKey<Block> incorrectDrops;
    private TagKey<Item> repairItemsTag;

    public CustomToolMaterial(String name, int harvestLevel, int durability, float damage, float speed, int enchantability, TagKey<Block> incorrectDrops) {
        this.name = name;
        this.harvestLevel = harvestLevel;
        this.durability = durability;
        this.damage = damage;
        this.speed = speed;
        this.enchantability = enchantability;
        this.incorrectDrops = incorrectDrops;
    }

    public String getName() {
        return this.name;
    }

    public int getUses() {
        return this.durability;
    }

    public float getSpeed() {
        return this.speed;
    }

    public float getAttackDamageBonus() {
        return this.damage;
    }

    public TagKey<Block> getIncorrectBlocksForDrops() {
        return this.incorrectDrops;
    }

    public int getLevel() {
        return this.harvestLevel;
    }

    public int getEnchantmentValue() {
        return this.enchantability;
    }

    public Ingredient getRepairIngredient() {
        return this.ingredient == null ? Ingredient.of(java.util.stream.Stream.empty()) : this.ingredient;
    }

    public void setRepairMaterial(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public void setRepairItemsTag(TagKey<Item> repairItemsTag) {
        this.repairItemsTag = repairItemsTag;
    }

    /**
     * @param repairItems required for {@link ToolMaterial}; use a dedicated item tag when no repair is desired.
     */
    public ToolMaterial toToolMaterial(TagKey<Item> repairItems) {
        TagKey<Item> tag = this.repairItemsTag != null ? this.repairItemsTag : repairItems;
        return new ToolMaterial(this.incorrectDrops, this.durability, this.speed, this.damage, this.enchantability, tag);
    }
}
