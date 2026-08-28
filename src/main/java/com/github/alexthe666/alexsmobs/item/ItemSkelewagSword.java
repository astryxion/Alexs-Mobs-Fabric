package com.github.alexthe666.alexsmobs.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public class ItemSkelewagSword extends SwordItem {

    private final Multimap<Attribute, AttributeModifier> skelewagModifiers;

    public ItemSkelewagSword(Item.Properties props) {
        super(Tiers.IRON, 2, 0.0F, props);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 3.5D, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", 0.0D, AttributeModifier.Operation.ADDITION));
        this.skelewagModifiers = builder.build();
    }

    public float getDamage() {
        return 3.5F;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.skelewagModifiers : super.getDefaultAttributeModifiers(slot);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairStack) {
        return repairStack.is(Items.BONE);
    }
}
