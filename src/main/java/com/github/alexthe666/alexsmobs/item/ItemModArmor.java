package com.github.alexthe666.alexsmobs.item;

import static com.github.alexthe666.alexsmobs.item.AMArmorMaterials.*;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Custom armor item for AlexsMobs.
 * Custom armor rendering is registered via ArmorRenderer in ClientProxy.
 */
public class ItemModArmor extends Item {

    private final ArmorMaterial armorMaterial;
    private final ArmorType armorType;

    public ItemModArmor(ArmorMaterial material, ArmorType type) {
        this(material, type, new Item.Properties());
    }

    public ItemModArmor(ArmorMaterial material, ArmorType type, Item.Properties properties) {
        super(applyExtraAttributes(properties.humanoidArmor(material, type).stacksTo(1), material, type));
        this.armorMaterial = material;
        this.armorType = type;
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

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flagIn) {
        if (armorMaterial == CENTIPEDE_ARMOR_MATERIAL) {
            tooltip.accept(Component.translatable("item.alexsmobs.centipede_leggings.desc").withStyle(ChatFormatting.GRAY));
        }
        if (armorMaterial == EMU_ARMOR_MATERIAL) {
            tooltip.accept(Component.translatable("item.alexsmobs.emu_leggings.desc").withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flagIn);
        if (armorMaterial == ROADRUNNER_ARMOR_MATERIAL) {
            tooltip.accept(Component.translatable("item.alexsmobs.roadrunner_boots.desc").withStyle(ChatFormatting.BLUE));
        }
        if (armorMaterial == RACCOON_ARMOR_MATERIAL) {
            tooltip.accept(Component.translatable("item.alexsmobs.frontier_cap.desc").withStyle(ChatFormatting.BLUE));
        }
        if (armorMaterial == FROSTSTALKER_ARMOR_MATERIAL) {
            tooltip.accept(Component.translatable("item.alexsmobs.froststalker_helmet.desc").withStyle(ChatFormatting.AQUA));
        }
        if (armorMaterial == ROCKY_ARMOR_MATERIAL) {
            tooltip.accept(Component.translatable("item.alexsmobs.rocky_chestplate.desc").withStyle(ChatFormatting.GRAY));
        }
        if (armorMaterial == SOMBRERO_ARMOR_MATERIAL && AlexsMobs.isAprilFools()) {
            tooltip.accept(Component.translatable("item.alexsmobs.sombrero.special_desc").withStyle(ChatFormatting.GRAY));
        }
        if (armorMaterial == FLYING_FISH_MATERIAL) {
            tooltip.accept(Component.translatable("item.alexsmobs.flying_fish_boots.desc").withStyle(ChatFormatting.GRAY));
        }
        if (armorMaterial == NOVELTY_HAT_MATERIAL) {
            tooltip.accept(Component.translatable("item.alexsmobs.novelty_hat.desc").withStyle(ChatFormatting.GRAY));
        }
        if (armorMaterial == KIMONO_MATERIAL) {
            tooltip.accept(Component.translatable("item.alexsmobs.unsettling_kimono.desc").withStyle(ChatFormatting.GRAY));
        }
    }

    private static Item.Properties applyExtraAttributes(Item.Properties properties, ArmorMaterial material, ArmorType type) {
        Item.Properties props = properties.humanoidArmor(material, type).stacksTo(1);
        if (buildExtraAttributeModifiers(material, type) == null) {
            return props;
        }
        return props.component(DataComponents.ATTRIBUTE_MODIFIERS, buildFullAttributeModifiers(material, type));
    }

    private static ItemAttributeModifiers buildFullAttributeModifiers(ArmorMaterial material, ArmorType type) {
        EquipmentSlotGroup slotGroup = EquipmentSlotGroup.bySlot(type.getSlot());
        Identifier base = Identifier.fromNamespaceAndPath(AlexsMobs.MODID, materialName(material) + "_" + type.name().toLowerCase());
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder()
                .add(Attributes.ARMOR, new AttributeModifier(base.withSuffix("_armor"), armorDefense(material, type), AttributeModifier.Operation.ADD_VALUE), slotGroup)
                .add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(base.withSuffix("_tough"), armorToughness(material), AttributeModifier.Operation.ADD_VALUE), slotGroup);
        ItemAttributeModifiers extras = buildExtraAttributeModifiers(material, type);
        if (extras != null) {
            for (ItemAttributeModifiers.Entry entry : extras.modifiers()) {
                builder.add(entry.attribute(), entry.modifier(), entry.slot());
            }
        }
        return builder.build();
    }

    private static int armorDefense(ArmorMaterial material, ArmorType type) {
        if (material == CROCODILE_ARMOR_MATERIAL) return type == ArmorType.CHESTPLATE ? 7 : 0;
        if (material == MOOSE_ARMOR_MATERIAL) return type == ArmorType.HELMET ? 3 : 0;
        if (material == FLYING_FISH_MATERIAL) return type == ArmorType.BOOTS ? 3 : 0;
        if (material == KIMONO_MATERIAL) return type == ArmorType.CHESTPLATE ? 10 : 0;
        return 0;
    }

    private static float armorToughness(ArmorMaterial material) {
        if (material == CROCODILE_ARMOR_MATERIAL) return 1.0F;
        if (material == MOOSE_ARMOR_MATERIAL) return 0.5F;
        return 0.0F;
    }

    @Nullable
    private static ItemAttributeModifiers buildExtraAttributeModifiers(ArmorMaterial material, ArmorType type) {
        EquipmentSlotGroup slotGroup = EquipmentSlotGroup.bySlot(type.getSlot());
        Identifier base = Identifier.fromNamespaceAndPath(AlexsMobs.MODID, materialName(material) + "_" + type.name().toLowerCase());
        ItemAttributeModifiers.Builder builder = null;
        if (material == CROCODILE_ARMOR_MATERIAL) {
            builder = ItemAttributeModifiers.builder()
                    .add(Attributes.WATER_MOVEMENT_EFFICIENCY, new AttributeModifier(
                            base.withSuffix("_swim"), 1.0, AttributeModifier.Operation.ADD_VALUE), slotGroup);
        } else if (material == MOOSE_ARMOR_MATERIAL) {
            builder = ItemAttributeModifiers.builder()
                    .add(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(
                            base.withSuffix("_knockback"), 2.0, AttributeModifier.Operation.ADD_VALUE), slotGroup);
        } else if (material == FLYING_FISH_MATERIAL) {
            builder = ItemAttributeModifiers.builder()
                    .add(Attributes.WATER_MOVEMENT_EFFICIENCY, new AttributeModifier(
                            base.withSuffix("_swim"), 0.5, AttributeModifier.Operation.ADD_VALUE), slotGroup);
        } else if (material == KIMONO_MATERIAL) {
            builder = ItemAttributeModifiers.builder()
                    .add(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(
                            base.withSuffix("_block_reach"), 2.0, AttributeModifier.Operation.ADD_VALUE), slotGroup)
                    .add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(
                            base.withSuffix("_entity_reach"), 2.0, AttributeModifier.Operation.ADD_VALUE), slotGroup);
        }
        return builder == null ? null : builder.build();
    }

    private static String materialName(ArmorMaterial material) {
        if (material == CROCODILE_ARMOR_MATERIAL) return "crocodile";
        if (material == MOOSE_ARMOR_MATERIAL) return "moose";
        if (material == FLYING_FISH_MATERIAL) return "flying_fish";
        if (material == KIMONO_MATERIAL) return "kimono";
        return "armor";
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
