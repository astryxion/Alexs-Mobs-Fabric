package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemModArmor extends ArmorItem {
    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{
            UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
            UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
            UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
            UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")
    };

    private Multimap<Attribute, AttributeModifier> attributeMapCroc;
    private Multimap<Attribute, AttributeModifier> attributeMapMoose;
    private Multimap<Attribute, AttributeModifier> attributeMapFlyingFish;
    private Multimap<Attribute, AttributeModifier> attributeMapKimono;
    private final AMArmorMaterial armorMaterialWrapper;

    public ItemModArmor(AMArmorMaterial armorMaterial, ArmorItem.Type slot) {
        super(armorMaterial, slot, new Item.Properties()
            .stacksTo(1)
            .durability(armorMaterial.getDurabilityForType(slot)));
        this.armorMaterialWrapper = armorMaterial;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (this.armorMaterialWrapper == AMItemRegistry.CENTIPEDE_ARMOR_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.centipede_leggings.desc").withStyle(ChatFormatting.GRAY));
        }
        if (this.armorMaterialWrapper == AMItemRegistry.EMU_ARMOR_MATERIAL) {
            tooltip.add(Component.translatable("item.alexsmobs.emu_leggings.desc").withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
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

    private void buildCrocAttributes(AMArmorMaterial materialIn) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        UUID uuid = ARMOR_MODIFIERS[this.type.ordinal()];
        builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", materialIn.getDefenseForType(this.type), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", materialIn.getToughness(), AttributeModifier.Operation.ADDITION));
        if (AMItemRegistry.SWIM_SPEED_ATTRIBUTE != null) {
            builder.put(AMItemRegistry.SWIM_SPEED_ATTRIBUTE, new AttributeModifier(uuid, "Swim speed", 1.0D, AttributeModifier.Operation.ADDITION));
        }
        if (this.armorMaterialWrapper.knockbackResistance > 0) {
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance", this.armorMaterialWrapper.knockbackResistance, AttributeModifier.Operation.ADDITION));
        }
        attributeMapCroc = builder.build();
    }

    private void buildFlyingFishAttributes(AMArmorMaterial materialIn) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        UUID uuid = ARMOR_MODIFIERS[this.type.ordinal()];
        builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", materialIn.getDefenseForType(this.type), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", materialIn.getToughness(), AttributeModifier.Operation.ADDITION));
        if (AMItemRegistry.SWIM_SPEED_ATTRIBUTE != null) {
            builder.put(AMItemRegistry.SWIM_SPEED_ATTRIBUTE, new AttributeModifier(uuid, "Swim speed", 0.5D, AttributeModifier.Operation.ADDITION));
        }
        attributeMapFlyingFish = builder.build();
    }

    private void buildMooseAttributes(AMArmorMaterial materialIn) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        UUID uuid = ARMOR_MODIFIERS[this.type.ordinal()];
        builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", materialIn.getDefenseForType(this.type), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", materialIn.getToughness(), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(uuid, "Knockback", 2.0D, AttributeModifier.Operation.ADDITION));
        if (this.armorMaterialWrapper.knockbackResistance > 0) {
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance", this.armorMaterialWrapper.knockbackResistance, AttributeModifier.Operation.ADDITION));
        }
        attributeMapMoose = builder.build();
    }

    private void buildKimonoAttributes(AMArmorMaterial materialIn) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        UUID uuid = ARMOR_MODIFIERS[this.type.ordinal()];
        builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", materialIn.getDefenseForType(this.type), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", materialIn.getToughness(), AttributeModifier.Operation.ADDITION));
        if (AMItemRegistry.BLOCK_REACH_ATTRIBUTE != null) {
            builder.put(AMItemRegistry.BLOCK_REACH_ATTRIBUTE, new AttributeModifier(uuid, "Block Reach distance", 2.0D, AttributeModifier.Operation.ADDITION));
        }
        if (AMItemRegistry.ENTITY_REACH_ATTRIBUTE != null) {
            builder.put(AMItemRegistry.ENTITY_REACH_ATTRIBUTE, new AttributeModifier(uuid, "Entity Reach distance", 2.0D, AttributeModifier.Operation.ADDITION));
        }
        attributeMapKimono = builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        if (equipmentSlot == this.type.getSlot()) {
            if (this.armorMaterialWrapper == AMItemRegistry.CROCODILE_ARMOR_MATERIAL) {
                if (attributeMapCroc == null) {
                    buildCrocAttributes(AMItemRegistry.CROCODILE_ARMOR_MATERIAL);
                }
                return attributeMapCroc;
            }
            if (this.armorMaterialWrapper == AMItemRegistry.MOOSE_ARMOR_MATERIAL) {
                if (attributeMapMoose == null) {
                    buildMooseAttributes(AMItemRegistry.MOOSE_ARMOR_MATERIAL);
                }
                return attributeMapMoose;
            }
            if (this.armorMaterialWrapper == AMItemRegistry.FLYING_FISH_MATERIAL) {
                if (attributeMapFlyingFish == null) {
                    buildFlyingFishAttributes(AMItemRegistry.FLYING_FISH_MATERIAL);
                }
                return attributeMapFlyingFish;
            }
            if (this.armorMaterialWrapper == AMItemRegistry.KIMONO_MATERIAL) {
                if (attributeMapKimono == null) {
                    buildKimonoAttributes(AMItemRegistry.KIMONO_MATERIAL);
                }
                return attributeMapKimono;
            }
        }
        return super.getDefaultAttributeModifiers(equipmentSlot);
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

    public AMArmorMaterial getArmorMaterialWrapper() {
        return armorMaterialWrapper;
    }
}
