package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCatfish;
import com.github.alexthe666.alexsmobs.entity.EntityLobster;
import com.github.alexthe666.alexsmobs.entity.util.TerrapinTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Bucketable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ItemModFishBucket extends MobBucketItem {

    private final Supplier<? extends EntityType<? extends Mob>> fishTypeSupplier;

    public ItemModFishBucket(Supplier<? extends EntityType<? extends Mob>> fishTypeIn, Fluid fluid, Item.Properties builder) {
        super(fishTypeIn.get(), fluid, SoundEvents.BUCKET_EMPTY_FISH, builder.stacksTo(1));
        this.fishTypeSupplier = fishTypeIn;
    }
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        EntityType<? extends Mob> fishType = fishTypeSupplier.get();
        CompoundTag compoundnbt = stack.getOrDefault(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY).copyTag();
        if (fishType == AMEntityRegistry.LOBSTER) {
            if (compoundnbt.contains("BucketVariantTag")) {
                int i = compoundnbt.getIntOr("BucketVariantTag", 0);
                String s = "entity.alexsmobs.lobster.variant_" + EntityLobster.getVariantName(i);
                tooltip.add((Component.translatable(s)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
            }
        }
        if (fishType == AMEntityRegistry.TERRAPIN) {
            if (compoundnbt.contains("TerrapinData")) {
                int i = compoundnbt.getCompoundOrEmpty("TerrapinData").getIntOr("TurtleType", 0);
                tooltip.add((Component.translatable(TerrapinTypes.values()[Mth.clamp(i, 0, TerrapinTypes.values().length - 1)].getTranslationName())).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
            }
        }
        if (fishType == AMEntityRegistry.COMB_JELLY) {
            if (compoundnbt.contains("BucketVariantTag")) {
                int i = compoundnbt.getIntOr("BucketVariantTag", 0);
                String s = "entity.alexsmobs.comb_jelly.variant_" + i;
                tooltip.add((Component.translatable(s)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
            }
        }
    }

    @Override
    public void checkExtraContent(@Nullable LivingEntity player, Level level, ItemStack stack, BlockPos pos) {
        if (level instanceof ServerLevel) {
            this.spawnFish((ServerLevel)level, stack, pos);
            level.gameEvent(player, GameEvent.ENTITY_PLACE, pos);
        }
    }

    private void spawnFish(ServerLevel serverLevel, ItemStack stack, BlockPos pos) {
        Entity entity = fishTypeSupplier.get().spawn(serverLevel, stack, null, pos, EntitySpawnReason.BUCKET, true, false);
        if (entity instanceof Bucketable bucketable) {
            CompoundTag bucketTag = stack.getOrDefault(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY).copyTag();
            CompoundTag customTag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            CompoundTag merged = bucketTag.isEmpty() ? customTag.copy() : bucketTag.copy();
            if (!customTag.isEmpty()) {
                merged.merge(customTag);
            }
            bucketable.loadFromBucketTag(merged);
            bucketable.setFromBucket(true);
        }
        addExtraAttributes(entity, stack);
    }

    private void addExtraAttributes(Entity entity, ItemStack stack) {
        if (entity instanceof EntityCatfish catfish) {
            Item item = stack.getItem();
            if (item == AMItemRegistry.SMALL_CATFISH_BUCKET) {
                catfish.setCatfishSize(0);
            } else if (item == AMItemRegistry.MEDIUM_CATFISH_BUCKET) {
                catfish.setCatfishSize(1);
            } else if (item == AMItemRegistry.LARGE_CATFISH_BUCKET) {
                catfish.setCatfishSize(2);
            }
        }
    }


}
