package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.inventory.MenuTransmutationTable;
import com.github.alexthe666.alexsmobs.message.MessageUpdateTransmutablesToDisplay;
import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityTransmutationTable;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class BlockTransmutationTable extends BaseEntityBlock implements AMSpecialRenderBlock {

    public static final MapCodec<BlockTransmutationTable> CODEC = BlockBehaviour.simpleCodec(BlockTransmutationTable::new);

    private static final Component CONTAINER_TITLE = Component.translatable("alexsmobs.container.transmutation_table");
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape BASE_AABB = Block.box(1, 0, 1, 15, 5, 15);
    private static final VoxelShape ARMS_NS = Block.box(1, 5, 5.5F, 15, 16, 10.5F);
    private static final VoxelShape ARMS_EW = Block.box(5.5F, 5, 1, 10.5F, 16, 15);
    private static final VoxelShape NS_AABB = Shapes.or(BASE_AABB, ARMS_NS);
    private static final VoxelShape EW_AABB = Shapes.or(BASE_AABB, ARMS_EW);

    public BlockTransmutationTable(BlockBehaviour.Properties properties) {
        super(properties);
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
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(level, pos));
            player.awardStat(Stats.INTERACT_WITH_LOOM);
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileEntityTransmutationTable table) {
                if (!table.hasPossibilities()) {
                    table.setRerollPlayerUUID(player.getUUID());
                    TileEntityTransmutationTable.commonTick(level, pos, state, table);
                }
                if (player instanceof ServerPlayer serverPlayer) {
                    AlexsMobs.sendNonLocal(new MessageUpdateTransmutablesToDisplay(player.getId(), table.getPossibility(0), table.getPossibility(1), table.getPossibility(2)), serverPlayer);
                }
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
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        ItemStack tool = builder.getOptionalParameter(LootContextParams.TOOL);
        if (tool != null) {
            ServerLevel level = builder.getLevel();
            if (EnchantmentHelper.getItemEnchantmentLevel(level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.SILK_TOUCH), tool) > 0) {
                return List.of(new ItemStack(this));
            }
        }
        return List.of(new ItemStack(Items.NETHER_STAR));
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && AMConfig.transmutingTableExplodes) {
            level.explode(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 3F, false, Level.ExplosionInteraction.BLOCK);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}

