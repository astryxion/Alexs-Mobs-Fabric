package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityFlutter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class ItemFlutterPot extends Item implements DispensibleContainerItem {

    public ItemFlutterPot(Properties builder) {
        super(builder.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        if(!world.isClientSide()){
            if(this.placeFish((ServerLevel)world, context.getItemInHand(), blockpos) && (context.getPlayer() == null || !context.getPlayer().isCreative())){
                context.getItemInHand().shrink(1);
            }
            return world.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }else{
            return InteractionResult.PASS;
        }

    }

    protected void playEmptySound(@Nullable Player player, LevelAccessor worldIn, BlockPos pos) {
        worldIn.playSound(player, pos, SoundEvents.BUCKET_EMPTY_FISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
    }

    private boolean placeFish(ServerLevel worldIn, ItemStack stack, BlockPos pos) {
        Entity entity = AMEntityRegistry.FLUTTER.spawn(worldIn, stack, (Player)null, pos, EntitySpawnReason.BUCKET, true, false);
        if (entity != null && entity instanceof EntityFlutter) {
            CompoundTag compoundnbt = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
            if (compoundnbt.contains("FlutterData")) {
                ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, worldIn.registryAccess(), compoundnbt.getCompoundOrEmpty("FlutterData"));
                ((EntityFlutter) entity).readFlutterDataFromItem(input);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean emptyContents(@Nullable LivingEntity holder, Level level, BlockPos pos, @Nullable BlockHitResult hitResult) {
        return false;
    }
}
