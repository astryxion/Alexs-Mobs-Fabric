package com.github.alexthe666.alexsmobs.item;

import static com.github.alexthe666.alexsmobs.item.AMArmorMaterials.*;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

import javax.annotation.Nullable;

/**
 * Custom armor item for AlexsMobs.
 * Custom armor rendering is registered via RegisterClientExtensionsEvent in ClientProxy.
 * Entity textures are resolved by {@link com.github.alexthe666.alexsmobs.client.render.item.CustomArmorRenderProperties}.
 */
public class ItemModArmor extends Item {

    private final ArmorMaterial armorMaterial;

    public ItemModArmor(ArmorMaterial material, ArmorType type) {
        this(material, type, new Item.Properties());
    }

    public ItemModArmor(ArmorMaterial material, ArmorType type, Item.Properties properties) {
        super(properties.humanoidArmor(material, type).stacksTo(1));
        this.armorMaterial = material;
    }

    public ArmorMaterial getMaterial() {
        return armorMaterial;
    }

    public int getEnchantmentValue() {
        return 15;
    }

    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    /**
     * Texture path for entity armor rendering (kangaroo layers, etc.).
     */
    @Nullable
    public Identifier getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, @Nullable String overlaySuffix, boolean innerModel) {
        ArmorMaterial mat = this.getMaterial();

        Identifier base;
        if (mat == CROCODILE_ARMOR_MATERIAL) {
            base = Identifier.fromNamespaceAndPath("alexsmobs", "textures/armor/crocodile_chestplate.png");
        } else if (mat == ROADRUNNER_ARMOR_MATERIAL) {
            base = Identifier.fromNamespaceAndPath("alexsmobs", "textures/armor/roadrunner_boots.png");
        } else if (mat == CENTIPEDE_ARMOR_MATERIAL) {
            base = Identifier.fromNamespaceAndPath("alexsmobs", "textures/armor/centipede_leggings.png");
        } else if (mat == MOOSE_ARMOR_MATERIAL) {
            base = Identifier.fromNamespaceAndPath("alexsmobs", "textures/armor/moose_headgear.png");
        } else if (mat == RACCOON_ARMOR_MATERIAL) {
            base = Identifier.fromNamespaceAndPath("alexsmobs", "textures/armor/frontier_cap.png");
        } else if (mat == SOMBRERO_ARMOR_MATERIAL) {
            base = Identifier.fromNamespaceAndPath("alexsmobs", "textures/armor/sombrero.png");
        } else if (mat == SPIKED_TURTLE_SHELL_ARMOR_MATERIAL) {
            base = Identifier.fromNamespaceAndPath("alexsmobs", "textures/armor/spiked_turtle_shell.png");
        } else if (mat == FEDORA_ARMOR_MATERIAL) {
            base = Identifier.fromNamespaceAndPath("alexsmobs", "textures/armor/fedora.png");
        } else if (mat == EMU_ARMOR_MATERIAL) {
            base = Identifier.fromNamespaceAndPath("alexsmobs", "textures/armor/emu_leggings.png");
        } else if (mat == FROSTSTALKER_ARMOR_MATERIAL) {
            base = Identifier.fromNamespaceAndPath("alexsmobs", "textures/armor/froststalker_helmet.png");
        } else if (mat == ROCKY_ARMOR_MATERIAL) {
            base = Identifier.fromNamespaceAndPath("alexsmobs", "textures/armor/rocky_chestplate.png");
        } else if (mat == FLYING_FISH_MATERIAL) {
            base = Identifier.fromNamespaceAndPath("alexsmobs", "textures/armor/flying_fish_boots.png");
        } else if (mat == NOVELTY_HAT_MATERIAL) {
            base = Identifier.fromNamespaceAndPath("alexsmobs", "textures/armor/novelty_hat.png");
        } else if (mat == KIMONO_MATERIAL) {
            base = Identifier.fromNamespaceAndPath("alexsmobs", "textures/armor/unsettling_kimono.png");
        } else {
            base = null;
        }

        if (base == null) {
            return null;
        }
        if ("overlay".equals(overlaySuffix)) {
            String path = base.getPath();
            int dot = path.lastIndexOf('.');
            if (dot > 0) {
                return Identifier.fromNamespaceAndPath(base.getNamespace(), path.substring(0, dot) + "_overlay" + path.substring(dot));
            }
        }
        return base;
    }
}
