package com.github.alexthe666.citadel.server.item;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class CustomArmorMaterial {
    private final String name;
    private final int durability;
    private final int[] damageReduction;
    private final int encantability;
    private final SoundEvent sound;
    private final float toughness;
    private Ingredient ingredient = null;
    public float knockbackResistance = 0.0F;

    public CustomArmorMaterial(String name, int durability, int[] damageReduction, int encantability, SoundEvent sound, float toughness, float knockbackResistance) {
        this.name = name;
        this.durability = durability;
        this.damageReduction = damageReduction;
        this.encantability = encantability;
        this.sound = sound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
    }

    public int getDurabilityForType(ArmorItem.Type slotIn) {
        return durability;
    }

    public int getDefenseForType(ArmorItem.Type slotIn) {
        return damageReduction[slotIn.ordinal()];
    }

    public int getEnchantmentValue() {
        return encantability;
    }

    public SoundEvent getEquipSound() {
        return sound;
    }

    public Ingredient getRepairIngredient() {
        return ingredient == null ? Ingredient.EMPTY : ingredient;
    }

    public void setRepairMaterial(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public String getName() {
        return name;
    }

    public float getToughness() {
        return toughness;
    }

    public float getKnockbackResistance() {
        return knockbackResistance;
    }

    /** Returns an ArmorMaterial record for use with 1.21.1 armor items. */
    public ArmorMaterial toArmorMaterial() {
        Map<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
        for (ArmorItem.Type type : ArmorItem.Type.values()) {
            if (type.ordinal() < damageReduction.length) {
                defense.put(type, damageReduction[type.ordinal()]);
            }
        }
        Holder<SoundEvent> equipSound = Holder.direct(sound);
        Supplier<Ingredient> repairSupplier = () -> (ingredient == null ? Ingredient.EMPTY : ingredient);
        return new ArmorMaterial(defense, encantability, equipSound, repairSupplier, List.of(), toughness, knockbackResistance);
    }
}
