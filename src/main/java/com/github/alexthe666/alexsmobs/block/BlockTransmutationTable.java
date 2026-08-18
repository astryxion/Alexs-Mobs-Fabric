package com.github.alexthe666.alexsmobs.block;

import com.mojang.serialization.MapCodec;
import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.inventory.MenuTransmutationTable;
import com.github.alexthe666.alexsmobs.network.MessageUpdateTransmutablesToDisplay;
import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityTransmutationTable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

import static net.minecraft.world.level.block.state.BlockBehaviour.simpleCodec;

public class BlockTransmutationTable extends BaseEntityBlock implements AMSpecialRenderBlock {
    public static final MapCodec<BlockTransmutationTable> CODEC = simpleCodec(BlockTransmutationTable::new);


    private static final Component CONTAINER_TITLE = Component.translatable("alexsmobs.container.transmutation_table");
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape BASE_AABB = Block.box(1, 0, 1, 15, 5, 15);
    private static final VoxelShape ARMS_NS = Block.box(1, 5, 5.5F, 15, 16, 10.5F);
    private static final VoxelShape ARMS_EW = Block.box(5.5F, 5, 1, 10.5F, 16, 15);
    private static final VoxelShape NS_AABB = Shapes.or(BASE_AABB, ARMS_NS);
    private static final VoxelShape EW_AABB = Shapes.or(BASE_AABB, ARMS_EW);

    public static BlockBehaviour.Properties defaultProperties() {
        return BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK).mapColor(DyeColor.BLACK).noOcclusion().lightLevel((block) -> 2).emissiveRendering((state, getter, pos) -> true).sound(SoundType.STONE).strength(1F).requiresCorrectToolForDrops();
    }

    public BlockTransmutationTable(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public MapCodec<BlockTransmutationTable> codec() {
        return CODEC;
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.Z ? NS_AABB : EW_AABB;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityTransmutationTable(pos, state);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(level, pos));
            player.awardStat(Stats.INTERACT_WITH_LOOM);
            BlockEntity te = level.getBlockEntity(pos);
            if(te instanceof TileEntityTransmutationTable){
                TileEntityTransmutationTable table = (TileEntityTransmutationTable)te;

                AlexsMobs.sendMSGToAll(new MessageUpdateTransmutablesToDisplay(player.getId(), table.getPossibility(0), table.getPossibility(1), table.getPossibility(2)));

            }
            return InteractionResult.CONSUME;
        }
    }

    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        BlockEntity te = level.getBlockEntity(pos);
        return new SimpleMenuProvider((i, inv, player) -> {
            return new MenuTransmutationTable(i, inv, ContainerLevelAccess.create(level, pos), player, te instanceof  TileEntityTransmutationTable ? (TileEntityTransmutationTable)te : null);
        }, CONTAINER_TITLE);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
        return createTickerHelper(p_152182_, AMTileEntityRegistry.TRANSMUTATION_TABLE, TileEntityTransmutationTable::commonTick);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && player.hasInfiniteMaterials()) {
            explodeOnBreak(level, pos);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
        super.playerDestroy(level, player, pos, state, te, stack);
        if (!level.isClientSide() && !player.hasInfiniteMaterials()) {
            explodeOnBreak(level, pos);
        }
    }

    private static void explodeOnBreak(Level level, BlockPos pos) {
        if (AMConfig.transmutingTableExplodes) {
            level.explode(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 3F, false, Level.ExplosionInteraction.BLOCK);
        }
    }
}

