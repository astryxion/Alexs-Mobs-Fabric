package com.github.alexthe666.alexsmobs.item;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Wrapper that builds and holds an ArmorMaterial record (1.21.1).
 * Exposes the same getters for ItemModArmor attribute builders.
 */
public class AMArmorMaterial {

    protected static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};
    private final String name;
    private final int durability;
    private final int[] damageReduction;
    private final int encantability;
    private final Holder<SoundEvent> soundHolder;
    private final float toughness;
    private Ingredient ingredient = null;
    public float knockbackResistance = 0.0F;
    private final ArmorMaterial materialRecord;
    private Holder<ArmorMaterial> holder;

    public AMArmorMaterial(String name, int durability, int[] damageReduction, int encantability, Holder<SoundEvent> soundHolder, float toughness) {
        this.name = name;
        this.durability = durability;
        this.damageReduction = damageReduction;
        this.encantability = encantability;
        this.soundHolder = soundHolder;
        this.toughness = toughness;
        this.knockbackResistance = 0;
        this.materialRecord = buildRecord();
    }

    public AMArmorMaterial(String name, int durability, int[] damageReduction, int encantability, SoundEvent sound, float toughness) {
        this(name, durability, damageReduction, encantability, net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound), toughness);
    }

    public AMArmorMaterial(String name, int durability, int[] damageReduction, int encantability, Holder<SoundEvent> soundHolder, float toughness, float knockbackResist) {
        this.name = name;
        this.durability = durability;
        this.damageReduction = damageReduction;
        this.encantability = encantability;
        this.soundHolder = soundHolder;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResist;
        this.materialRecord = buildRecord();
    }

    public AMArmorMaterial(String name, int durability, int[] damageReduction, int encantability, SoundEvent sound, float toughness, float knockbackResist) {
        this(name, durability, damageReduction, encantability, net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound), toughness, knockbackResist);
    }

    private ArmorMaterial buildRecord() {
        Map<ArmorItem.Type, Integer> defense = Map.of(
                ArmorItem.Type.HELMET, damageReduction[3],
                ArmorItem.Type.CHESTPLATE, damageReduction[2],
                ArmorItem.Type.LEGGINGS, damageReduction[1],
                ArmorItem.Type.BOOTS, damageReduction[0]
        );
        Supplier<Ingredient> repairSupplier = () -> ingredient == null ? Ingredient.EMPTY : ingredient;
        return new ArmorMaterial(defense, encantability, soundHolder, repairSupplier, List.of(), toughness, knockbackResistance);
    }

    /** Returns the ArmorMaterial record for use in ArmorItem constructor. */
    public ArmorMaterial getMaterial() {
        return materialRecord;
    }

    /** Set by AMItemRegistry when registering armor materials (1.21.1 requires Holder). */
    public void setHolder(Holder<ArmorMaterial> holder) {
        this.holder = holder;
    }

    public Holder<ArmorMaterial> getHolder() {
        return holder;
    }

    public int getDurabilityForType(ArmorItem.Type type) {
        return MAX_DAMAGE_ARRAY[type.ordinal()] * this.durability;
    }

    public int getDefenseForType(ArmorItem.Type type) {
        return this.damageReduction[type.ordinal()];
    }

    public int getEnchantmentValue() {
        return this.encantability;
    }

    public SoundEvent getEquipSound() {
        return this.soundHolder.value();
    }

    public Ingredient getRepairIngredient() {
        return this.ingredient == null ? Ingredient.EMPTY : this.ingredient;
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
}
