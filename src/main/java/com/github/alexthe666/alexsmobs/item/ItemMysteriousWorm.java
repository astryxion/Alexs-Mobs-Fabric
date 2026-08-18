package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Random;
import java.util.UUID;

public class ItemMysteriousWorm extends Item {
    public ItemMysteriousWorm(Properties props) {
        super(props);
    }

    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if(AMConfig.voidWormSummonable){
            String dim = entity.level().dimension().identifier().toString();
            if(AMConfig.voidWormSpawnDimensions.contains(dim) && entity.getY() < -60 && !entity.isRemoved() && entity.level() instanceof ServerLevel serverLevel){
                entity.kill(serverLevel);
                EntityVoidWorm worm = AMEntityRegistry.VOID_WORM.create(serverLevel, EntitySpawnReason.TRIGGERED);
                worm.setPos(entity.getX(), 0, entity.getZ());
                worm.setSegmentCount(25 + new Random().nextInt(15));
                worm.setXRot(-90.0F);
                worm.updatePostSummon = true;
                worm.setBaseMaxHealth(AMConfig.voidWormMaxHealth, true);

                if(!entity.level().isClientSide()){
                    Entity thrower = entity.getOwner();
                    if(thrower != null){
                        UUID uuid = thrower.getUUID();
                        if(entity.level().getPlayerByUUID(uuid) instanceof ServerPlayer){
                            AMAdvancementTriggerRegistry.VOID_WORM_SUMMON.trigger((ServerPlayer)entity.level().getPlayerByUUID(uuid));
                        }
                    }
                    entity.level().addFreshEntity(worm);
                }
            }
        }
        return false;
    }
}
