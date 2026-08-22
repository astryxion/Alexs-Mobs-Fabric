package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.resources.ResourceLocation;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ItemModArmor extends ArmorItem {
    private Multimap<Attribute, AttributeModifier> attributeMapCroc;
    private Multimap<Attribute, AttributeModifier> attributeMapMoose;
    private Multimap<Attribute, AttributeModifier> attributeMapFlyingFish;
    private Multimap<Attribute, AttributeModifier> attributeMapKimono;
    private final AMArmorMaterial armorMaterialWrapper;

    public ItemModArmor(AMArmorMaterial armorMaterial, ArmorItem.Type slot) {
        super(armorMaterial.getHolder(), slot, new Item.Properties()
            .stacksTo(1)
            .durability(armorMaterial.getDurabilityForType(slot)));
        this.armorMaterialWrapper = armorMaterial;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        if (this.armorMaterialWrapper == AMItemRegistry.CENTIPEDE_ARMOR_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.centipede_leggings.desc").withStyle(ChatFormatting.GRAY));
        }
        if (this.armorMaterialWrapper == AMItemRegistry.EMU_ARMOR_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.emu_leggings.desc").withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, context, tooltip, flagIn);
        if (this.armorMaterialWrapper == AMItemRegistry.ROADRUNNER_ARMOR_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.roadrunner_boots.desc").withStyle(ChatFormatting.BLUE));
        }
        if (this.armorMaterialWrapper == AMItemRegistry.RACCOON_ARMOR_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.frontier_cap.desc").withStyle(ChatFormatting.BLUE));
        }
        if (this.armorMaterialWrapper == AMItemRegistry.FROSTSTALKER_ARMOR_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.froststalker_helmet.desc").withStyle(ChatFormatting.AQUA));
        }
        if (this.armorMaterialWrapper == AMItemRegistry.ROCKY_ARMOR_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.rocky_chestplate.desc").withStyle(ChatFormatting.GRAY));
        }
        if (this.armorMaterialWrapper == AMItemRegistry.SOMBRERO_ARMOR_MATERIAL && AlexsMobs.isAprilFools()) {
            tooltip.add(Component.translatable("item.alexsmobs.sombrero.special_desc").withStyle(ChatFormatting.GRAY));
        }
        if (this.armorMaterialWrapper == AMItemRegistry.FLYING_FISH_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.flying_fish_boots.desc").withStyle(ChatFormatting.GRAY));
        }
        if (this.armorMaterialWrapper == AMItemRegistry.NOVELTY_HAT_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.novelty_hat.desc").withStyle(ChatFormatting.GRAY));
        }
        if (this.armorMaterialWrapper == AMItemRegistry.KIMONO_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.unsettling_kimono.desc").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("item.alexsmobs.unsettling_kimono.desc2").withStyle(ChatFormatting.GRAY));
        }
    }

    private static ResourceLocation armorModId(String path) {
        return ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, path);
    }

    private void buildCrocAttributes(AMArmorMaterial materialIn) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        ResourceLocation base = armorModId("croc_armor_" + type.ordinal());
        builder.put(Attributes.ARMOR.value(), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_armor"), materialIn.getDefenseForType(this.type), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ARMOR_TOUGHNESS.value(), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_toughness"), materialIn.getToughness(), AttributeModifier.Operation.ADD_VALUE));
        builder.put(AMItemRegistry.SWIM_SPEED_ATTRIBUTE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_swim"), 1, AttributeModifier.Operation.ADD_VALUE));
        if (this.armorMaterialWrapper.knockbackResistance > 0) {
            builder.put(Attributes.KNOCKBACK_RESISTANCE.value(), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_kb"), this.armorMaterialWrapper.knockbackResistance, AttributeModifier.Operation.ADD_VALUE));
        }
        attributeMapCroc = builder.build();
    }

    private void buildFlyingFishAttributes(AMArmorMaterial materialIn) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        ResourceLocation base = armorModId("flying_fish_armor_" + type.ordinal());
        builder.put(Attributes.ARMOR.value(), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_armor"), materialIn.getDefenseForType(this.type), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ARMOR_TOUGHNESS.value(), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_toughness"), materialIn.getToughness(), AttributeModifier.Operation.ADD_VALUE));
        builder.put(AMItemRegistry.SWIM_SPEED_ATTRIBUTE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_swim"), 0.5, AttributeModifier.Operation.ADD_VALUE));
        attributeMapFlyingFish = builder.build();
    }

    private void buildMooseAttributes(AMArmorMaterial materialIn) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        ResourceLocation base = armorModId("moose_armor_" + type.ordinal());
        builder.put(Attributes.ARMOR.value(), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_armor"), materialIn.getDefenseForType(this.type), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ARMOR_TOUGHNESS.value(), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_toughness"), materialIn.getToughness(), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ATTACK_KNOCKBACK.value(), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_kb"), 2, AttributeModifier.Operation.ADD_VALUE));
        if (this.armorMaterialWrapper.knockbackResistance > 0) {
            builder.put(Attributes.KNOCKBACK_RESISTANCE.value(), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_resist"), this.armorMaterialWrapper.knockbackResistance, AttributeModifier.Operation.ADD_VALUE));
        }
        attributeMapMoose = builder.build();
    }

    private void buildKimonoAttributes(AMArmorMaterial materialIn) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        ResourceLocation base = armorModId("kimono_armor_" + type.ordinal());
        builder.put(Attributes.ARMOR.value(), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_armor"), materialIn.getDefenseForType(this.type), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ARMOR_TOUGHNESS.value(), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_toughness"), materialIn.getToughness(), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.BLOCK_INTERACTION_RANGE.value(), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_block_reach"), 2, AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ENTITY_INTERACTION_RANGE.value(), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_entity_reach"), 2, AttributeModifier.Operation.ADD_VALUE));
        attributeMapKimono = builder.build();
    }

    private ItemAttributeModifiers toItemAttributeModifiers(Multimap<Attribute, AttributeModifier> map) {
        EquipmentSlotGroup group = EquipmentSlotGroup.bySlot(this.type.getSlot());
        ItemAttributeModifiers result = ItemAttributeModifiers.EMPTY;
        for (var e : map.entries()) {
            result = result.withModifierAdded(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(e.getKey()), e.getValue(), group);
        }
        return result;
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        if (this.armorMaterialWrapper == AMItemRegistry.CROCODILE_ARMOR_MATERIAL) {
            if (attributeMapCroc == null) {
                buildCrocAttributes(AMItemRegistry.CROCODILE_ARMOR_MATERIAL);
            }
            return toItemAttributeModifiers(attributeMapCroc);
        }
        if (this.armorMaterialWrapper == AMItemRegistry.MOOSE_ARMOR_MATERIAL) {
            if (attributeMapMoose == null) {
                buildMooseAttributes(AMItemRegistry.MOOSE_ARMOR_MATERIAL);
            }
            return toItemAttributeModifiers(attributeMapMoose);
        }
        if (this.armorMaterialWrapper == AMItemRegistry.FLYING_FISH_MATERIAL) {
            if (attributeMapFlyingFish == null) {
                buildFlyingFishAttributes(AMItemRegistry.FLYING_FISH_MATERIAL);
            }
            return toItemAttributeModifiers(attributeMapFlyingFish);
        }
        if (this.armorMaterialWrapper == AMItemRegistry.KIMONO_MATERIAL) {
            if (attributeMapKimono == null) {
                buildKimonoAttributes(AMItemRegistry.KIMONO_MATERIAL);
            }
            return toItemAttributeModifiers(attributeMapKimono);
        }
        return super.getDefaultAttributeModifiers();
    }

    @Nullable
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (this.armorMaterialWrapper == AMItemRegistry.CROCODILE_ARMOR_MATERIAL) {
            return "alexsmobs:textures/armor/crocodile_chestplate.png";
        } else if (this.armorMaterialWrapper == AMItemRegistry.ROADRUNNER_ARMOR_MATERIAL) {
            return "alexsmobs:textures/armor/roadrunner_boots.png";
        } else if (this.armorMaterialWrapper == AMItemRegistry.CENTIPEDE_ARMOR_MATERIAL) {
            return "alexsmobs:textures/armor/centipede_leggings.png";
        } else if (this.armorMaterialWrapper == AMItemRegistry.MOOSE_ARMOR_MATERIAL) {
            return "alexsmobs:textures/armor/moose_headgear.png";
        } else if (this.armorMaterialWrapper == AMItemRegistry.RACCOON_ARMOR_MATERIAL) {
            return "alexsmobs:textures/armor/frontier_cap.png";
        } else if (this.armorMaterialWrapper == AMItemRegistry.SOMBRERO_ARMOR_MATERIAL) {
            return "alexsmobs:textures/armor/sombrero.png";
        } else if (this.armorMaterialWrapper == AMItemRegistry.SPIKED_TURTLE_SHELL_ARMOR_MATERIAL) {
            return "alexsmobs:textures/armor/spiked_turtle_shell.png";
        } else if (this.armorMaterialWrapper == AMItemRegistry.FEDORA_ARMOR_MATERIAL) {
            return "alexsmobs:textures/armor/fedora.png";
        } else if (this.armorMaterialWrapper == AMItemRegistry.EMU_ARMOR_MATERIAL) {
            return "alexsmobs:textures/armor/emu_leggings.png";
        } else if (this.armorMaterialWrapper == AMItemRegistry.FROSTSTALKER_ARMOR_MATERIAL) {
            return "alexsmobs:textures/armor/froststalker_helmet.png";
        } else if (this.armorMaterialWrapper == AMItemRegistry.ROCKY_ARMOR_MATERIAL) {
            return "alexsmobs:textures/armor/rocky_chestplate.png";
        } else if (this.armorMaterialWrapper == AMItemRegistry.FLYING_FISH_MATERIAL) {
            return "alexsmobs:textures/armor/flying_fish_boots.png";
        } else if (this.armorMaterialWrapper == AMItemRegistry.NOVELTY_HAT_MATERIAL) {
            return "alexsmobs:textures/armor/novelty_hat.png";
        } else if (this.armorMaterialWrapper == AMItemRegistry.KIMONO_MATERIAL) {
            return "alexsmobs:textures/armor/unsettling_kimono.png";
        }
        return "minecraft:textures/models/armor/leather_layer_1.png";
    }

    /** For tooltip/attribute logic that needs the wrapper. */
    public AMArmorMaterial getArmorMaterialWrapper() {
        return armorMaterialWrapper;
    }
}
