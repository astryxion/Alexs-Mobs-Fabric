package com.github.alexthe666.citadel.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CitadelLecternBlock extends LecternBlock {

    public CitadelLecternBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
        return new CitadelLecternBlockEntity(pos, blockState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (level.isClientSide() && blockEntity instanceof CitadelLecternBlockEntity lecternBlockEntity && lecternBlockEntity.hasBook()) {
            ItemStack book = lecternBlockEntity.getBook();
            if (!book.isEmpty() && !player.getCooldowns().isOnCooldown(book)) {
                book.use(level, player, InteractionHand.MAIN_HAND);
            }
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        if (state.getValue(HAS_BOOK)) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof CitadelLecternBlockEntity lectern) {
                return lectern.getRedstoneSignal();
            }
        }
        return 0;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean isMoving) {
        if (state.getValue(POWERED)) {
            level.updateNeighborsAt(pos.below(), this);
        }
        super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
    }

    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
        return new ItemStack(Items.LECTERN);
    }
}
