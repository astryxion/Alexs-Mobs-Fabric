package com.github.alexthe666.citadel.server;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.server.block.CitadelLecternBlock;
import com.github.alexthe666.citadel.server.block.CitadelLecternBlockEntity;
import com.github.alexthe666.citadel.server.block.LecternBooks;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CitadelEvents {

    public static void onLivingTick(net.minecraft.world.entity.LivingEntity entity) {
        if (CitadelConstants.DEBUG) {
            if (entity instanceof Player) {
                CompoundTag tag = CitadelEntityData.getCitadelTag(entity);
                tag.putInt("CitadelInt", tag.getInt("CitadelInt") + 1);
                Citadel.LOGGER.debug("Citadel Data Tag tracker example: " + tag.getInt("CitadelInt"));
            }
        }
    }

    public static void register() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = level.getBlockState(pos);
            ItemStack stack = player.getItemInHand(hand);
            if (state.is(Blocks.LECTERN) && LecternBooks.isLecternBook(stack)) {
                player.getCooldowns().addCooldown(stack.getItem(), 1);
                if (level.getBlockEntity(pos) instanceof LecternBlockEntity oldBe && !oldBe.hasBook()) {
                    BlockState newLectern = Citadel.LECTERN.defaultBlockState().setValue(CitadelLecternBlock.FACING, state.getValue(LecternBlock.FACING)).setValue(CitadelLecternBlock.POWERED, state.getValue(LecternBlock.POWERED)).setValue(CitadelLecternBlock.HAS_BOOK, true);
                    level.setBlockAndUpdate(pos, newLectern);
                    CitadelLecternBlockEntity newBe = new CitadelLecternBlockEntity(pos, newLectern);
                    ItemStack bookCopy = stack.copy();
                    bookCopy.setCount(1);
                    newBe.setBook(bookCopy);
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                    level.setBlockEntity(newBe);
                    player.swing(hand, true);
                    level.playSound(null, pos, SoundEvents.BOOK_PUT, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.PASS;
        });
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            if (oldPlayer != null && CitadelEntityData.getCitadelTag(oldPlayer) != null) {
                CitadelEntityData.setCitadelTag(newPlayer, CitadelEntityData.getCitadelTag(oldPlayer));
            }
        });
    }
}
