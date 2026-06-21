package com.github.alexthe666.alexsmobs.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;

import java.util.function.Consumer;

public class ItemGhostlyPickaxe extends Item {

    public ItemGhostlyPickaxe(Item.Properties props) {
        super(props.durability(700).pickaxe(ToolMaterial.IRON, 1.0F, -2.8F));
    }

    public static boolean shouldStoreInGhost(LivingEntity player, ItemStack stack) {
        return player instanceof Player && ((Player) player).getInventory().getFreeSlot() == -1;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState blockState) {
        return blockState.is(BlockTags.MINEABLE_WITH_PICKAXE) ? 20.0F : 1.0F;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity user) {
        if (shouldStoreInGhost(user, stack)) {
            if (user instanceof Player player) {
                player.awardStat(Stats.BLOCK_MINED.get(state.getBlock()));
                player.causeFoodExhaustion(0.005F);
            }
            if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
                BlockEntity blockentity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
                Block.getDrops(state, serverLevel, pos, blockentity, user, stack).forEach((item) -> {
                    putItemInGhostInventoryOrDrop(user, stack, item, serverLevel.registryAccess());
                });
                state.spawnAfterBreak(serverLevel, pos, stack, true);
            }
        }
        return super.mineBlock(stack, level, state, pos, user);
    }

    private static void putItemInGhostInventoryOrDrop(LivingEntity user, ItemStack pickaxe, ItemStack item, HolderLookup.Provider registries) {
        NonNullList<ItemStack> slots = readGhostSlots(pickaxe, registries);
        SimpleContainer container = new SimpleContainer(9);
        for (int i = 0; i < 9; i++) {
            container.setItem(i, slots.get(i));
        }
        if (user instanceof Player player) {
            if (player.getInventory().add(item)) {
                return;
            } else if (container.canAddItem(item)) {
                ItemStack leftover = container.addItem(item);
                for (int i = 0; i < 9; i++) {
                    slots.set(i, container.getItem(i));
                }
                writeGhostSlots(pickaxe, slots);
                item = leftover;
            }
        }
        if (!item.isEmpty() && user.level() instanceof ServerLevel serverLevel) {
            user.spawnAtLocation(serverLevel, item);
        }
    }

    private static NonNullList<ItemStack> readGhostSlots(ItemStack stack, HolderLookup.Provider registries) {
        NonNullList<ItemStack> list = NonNullList.withSize(9, ItemStack.EMPTY);
        ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
        if (contents != null) {
            contents.copyInto(list);
            return list;
        }
        net.minecraft.world.item.component.CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
        if (custom != null) {
            CompoundTag tag = custom.copyTag();
            if (tag.contains("Items")) {
                ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, registries, tag);
                ContainerHelper.loadAllItems(input, list);
            }
        }
        return list;
    }

    private static void writeGhostSlots(ItemStack stack, NonNullList<ItemStack> slots) {
        stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(slots));
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, net.minecraft.world.entity.EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (entity instanceof Player player) {
            if (player.tickCount % 3 == 0) {
                NonNullList<ItemStack> slots = readGhostSlots(stack, level.registryAccess());
                SimpleContainer container = new SimpleContainer(9);
                for (int i = 0; i < 9; i++) {
                    container.setItem(i, slots.get(i));
                }
                boolean flag = false;
                for (int slotI = 0; slotI < container.getContainerSize(); slotI++) {
                    ItemStack stackAt = container.getItem(slotI);
                    if (!stackAt.isEmpty() && player.addItem(stackAt)) {
                        container.removeItem(slotI, stackAt.getCount());
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    for (int i = 0; i < 9; i++) {
                        slots.set(i, container.getItem(i));
                    }
                    writeGhostSlots(stack, slots);
                }
            }
        }
    }

    public boolean isValidRepairItem(ItemStack pickaxe, ItemStack stack) {
        return stack.is(Items.PHANTOM_MEMBRANE);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flagIn);
        HolderLookup.Provider registries = context.registries();
        NonNullList<ItemStack> slots = readGhostSlots(stack, registries);
        SimpleContainer container = new SimpleContainer(9);
        for (int i = 0; i < 9; i++) {
            container.setItem(i, slots.get(i));
        }
        int i = 0;
        int j = 0;
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack itemstack = container.getItem(slot);
            if (!itemstack.isEmpty()) {
                ++j;
                if (i <= 4) {
                    ++i;
                    MutableComponent mutablecomponent = itemstack.getHoverName().copy();
                    mutablecomponent.append(" x").append(String.valueOf(itemstack.getCount()));
                    tooltip.accept(mutablecomponent.withStyle(ChatFormatting.DARK_AQUA));
                }
            }
        }
        if (j - i > 0) {
            tooltip.accept(Component.translatable("container.shulkerBox.more", j - i).withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.ITALIC));
        }
    }

    private void dropAllContents(Level level, Vec3 vec3, ItemStack pickaxe) {
        HolderLookup.Provider registries = level.registryAccess();
        NonNullList<ItemStack> slots = readGhostSlots(pickaxe, registries);
        for (int slot = 0; slot < 9; slot++) {
            ItemStack itemstack = slots.get(slot);
            if (!itemstack.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(level, vec3.x, vec3.y, vec3.z, itemstack.copy());
                if (level.addFreshEntity(itemEntity)) {
                    slots.set(slot, ItemStack.EMPTY);
                }
            }
        }
        writeGhostSlots(pickaxe, slots);
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity) {
        dropAllContents(itemEntity.level(), itemEntity.position(), itemEntity.getItem());
    }
}
