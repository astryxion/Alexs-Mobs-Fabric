package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.BlockTerrapinEgg;
import net.minecraft.resources.RegistryOps;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TypedEntityData;

import java.util.stream.Stream;
import java.util.function.Consumer;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.phys.BlockHitResult;

public class AMBlockItem extends BlockItem implements CustomTabBehavior {

    private final Block blockSupplier;

    public AMBlockItem(Block blockSupplier, Item.Properties props) {
        super((Block)null, props);
        this.blockSupplier = blockSupplier;
    }

    @Override
    public Block getBlock() {
        return blockSupplier;
    }

    public boolean canFitInsideCraftingRemainingItems() {
        return !(blockSupplier instanceof ShulkerBoxBlock);
    }

    public void onDestroyed(ItemEntity p_150700_) {
        if (this.blockSupplier instanceof ShulkerBoxBlock) {
            ItemStack itemstack = p_150700_.getItem();
            TypedEntityData<?> blockEntityData = itemstack.get(DataComponents.BLOCK_ENTITY_DATA);
            CompoundTag compoundtag = blockEntityData != null ? blockEntityData.copyTagWithoutId() : null;
            if (compoundtag != null && compoundtag.getList("Items").isPresent()) {
                ListTag listtag = compoundtag.getList("Items").get();
                RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, p_150700_.level().registryAccess());
                Stream<ItemStack> stacks = listtag.stream()
                        .filter(CompoundTag.class::isInstance)
                        .map(CompoundTag.class::cast)
                        .flatMap(t -> ItemStack.CODEC.parse(ops, t).result().stream());
                ItemUtils.onContainerDestroyed(p_150700_, stacks);
            }
        }
    }


    public boolean canBeHurtBy(ItemStack stack, DamageSource damage) {
        return (!stack.is(AMBlockRegistry.TRANSMUTATION_TABLE.asItem()) || !damage.is(DamageTypeTags.IS_EXPLOSION));
    }

    @Override
    public void fillItemCategory(CreativeModeTab.Output contents) {
        if(blockSupplier.equals(AMBlockRegistry.SAND_CIRCLE) || blockSupplier.equals(AMBlockRegistry.RED_SAND_CIRCLE)){

        }else{
            contents.accept(this);
        }
    }

    public InteractionResult useOn(UseOnContext context) {
        return blockSupplier.equals(AMBlockRegistry.TRIOPS_EGGS) ? InteractionResult.PASS : super.useOn(context);
    }

    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if(blockSupplier.equals(AMBlockRegistry.TRIOPS_EGGS)){
            BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
            BlockHitResult blockhitresult1 = blockhitresult.withPosition(blockhitresult.getBlockPos().above());
            return super.useOn(new UseOnContext(player, hand, blockhitresult1));
        }else{
            return super.use(level, player, hand);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flagIn);
        if (blockSupplier instanceof BlockTerrapinEgg) {
            BlockTerrapinEgg.appendTerrapinEggTooltip(stack, context, tooltipDisplay, tooltip, flagIn);
        }
    }
}
