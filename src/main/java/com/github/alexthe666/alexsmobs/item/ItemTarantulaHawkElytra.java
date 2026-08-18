package com.github.alexthe666.alexsmobs.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.util.Unit;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;

public class ItemTarantulaHawkElytra extends Item {

    public static final ResourceKey<EquipmentAsset> TARANTULA_HAWK_ELYTRA_ASSET =
            ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath("alexsmobs", "tarantula_hawk_elytra"));

    public ItemTarantulaHawkElytra(Item.Properties props) {
        super(props.durability(800).rarity(Rarity.UNCOMMON)
                .component(DataComponents.GLIDER, Unit.INSTANCE)
                .component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.CHEST)
                        .setAsset(TARANTULA_HAWK_ELYTRA_ASSET)
                        .setEquipSound(SoundEvents.ARMOR_EQUIP_ELYTRA)
                        .setDamageOnHurt(false)
                        .build()));
    }

    public static boolean isUsable(ItemStack stack) {
        return stack.getDamageValue() < stack.getMaxDamage() - 1;
    }

    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand handIn) {
        return super.use(worldIn, playerIn, handIn);
    }

    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == AMItemRegistry.TARANTULA_HAWK_WING_FRAGMENT;
    }
}