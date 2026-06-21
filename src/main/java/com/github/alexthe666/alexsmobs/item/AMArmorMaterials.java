package com.github.alexthe666.alexsmobs.item;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Util;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.EnumMap;

/**
 * Armor materials for AlexsMobs.
 * Each material uses a custom {@link EquipmentAsset} id under {@code alexsmobs:<assetName>}.
 */
public class AMArmorMaterials {

    private static ArmorMaterial createMaterial(
            String assetName,
            int durability,
            int helmet,
            int chestplate,
            int leggings,
            int boots,
            int enchantmentValue,
            float toughness,
            float knockbackResistance) {
        ResourceKey<EquipmentAsset> assetKey =
                ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath("alexsmobs", assetName));
        return new ArmorMaterial(
                durability,
                Util.make(new EnumMap<>(ArmorType.class), map -> {
                    map.put(ArmorType.HELMET, helmet);
                    map.put(ArmorType.CHESTPLATE, chestplate);
                    map.put(ArmorType.LEGGINGS, leggings);
                    map.put(ArmorType.BOOTS, boots);
                    map.put(ArmorType.BODY, chestplate);
                }),
                enchantmentValue,
                SoundEvents.ARMOR_EQUIP_LEATHER,
                toughness,
                knockbackResistance,
                ItemTags.REPAIRS_LEATHER_ARMOR,
                assetKey);
    }

    public static final ArmorMaterial ROADRUNNER_ARMOR_MATERIAL =
            createMaterial("roadrunner_boots", 18, 3, 3, 3, 3, 20, 0F, 0F);

    public static final ArmorMaterial CROCODILE_ARMOR_MATERIAL =
            createMaterial("crocodile_chestplate", 22, 3, 7, 5, 2, 25, 1F, 0F);

    public static final ArmorMaterial CENTIPEDE_ARMOR_MATERIAL =
            createMaterial("centipede_leggings", 20, 6, 6, 6, 6, 22, 0.5F, 0F);

    public static final ArmorMaterial MOOSE_ARMOR_MATERIAL =
            createMaterial("moose_headgear", 19, 3, 3, 3, 3, 21, 0.5F, 0F);

    public static final ArmorMaterial RACCOON_ARMOR_MATERIAL =
            createMaterial("frontier_cap", 17, 3, 3, 3, 3, 21, 2.5F, 0F);

    public static final ArmorMaterial SOMBRERO_ARMOR_MATERIAL =
            createMaterial("sombrero", 14, 2, 2, 2, 2, 30, 0.5F, 0F);

    public static final ArmorMaterial SPIKED_TURTLE_SHELL_ARMOR_MATERIAL =
            createMaterial("spiked_turtle_shell", 35, 3, 3, 3, 3, 30, 1F, 0.2F);

    public static final ArmorMaterial EMU_ARMOR_MATERIAL =
            createMaterial("emu_leggings", 9, 4, 4, 4, 4, 20, 0.5F, 0F);

    public static final ArmorMaterial FEDORA_ARMOR_MATERIAL =
            createMaterial("fedora", 10, 2, 2, 2, 2, 30, 0.5F, 0F);

    public static final ArmorMaterial TARANTULA_HAWK_ELYTRA_MATERIAL =
            createMaterial("tarantula_hawk_elytra", 9, 3, 3, 3, 3, 5, 0F, 0F);

    public static final ArmorMaterial FROSTSTALKER_ARMOR_MATERIAL =
            createMaterial("froststalker_helmet", 9, 3, 3, 3, 3, 15, 0.5F, 0F);

    public static final ArmorMaterial ROCKY_ARMOR_MATERIAL =
            createMaterial("rocky_chestplate", 20, 3, 7, 5, 2, 10, 0.5F, 0F);

    public static final ArmorMaterial FLYING_FISH_MATERIAL =
            createMaterial("flying_fish_boots", 9, 1, 1, 1, 1, 8, 0F, 0F);

    public static final ArmorMaterial NOVELTY_HAT_MATERIAL =
            createMaterial("novelty_hat", 10, 2, 2, 2, 2, 30, 0F, 0F);

    public static final ArmorMaterial KIMONO_MATERIAL =
            createMaterial("unsettling_kimono", 8, 3, 3, 3, 3, 15, 0F, 0F);
}
