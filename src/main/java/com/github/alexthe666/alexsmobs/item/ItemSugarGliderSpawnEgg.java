package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntitySugarGlider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class ItemSugarGliderSpawnEgg extends SpawnEggItem {

    public ItemSugarGliderSpawnEgg(int backgroundColor, int highlightColor, Properties properties) {
        super(AMEntityRegistry.SUGAR_GLIDER, backgroundColor, highlightColor, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }
        ItemStack stack = context.getItemInHand();
        BlockPos clicked = context.getClickedPos();
        Direction face = context.getClickedFace();
        BlockPos spawnPos = clicked.relative(face);
        EntityType<?> type = this.getType(stack.getTag());
        EntitySugarGlider glider = (EntitySugarGlider) type.spawn(serverLevel, stack, context.getPlayer(), spawnPos, MobSpawnType.SPAWN_EGG, true, !spawnPos.equals(clicked) && face == Direction.UP);
        if (glider != null) {
            if (face.getAxis().isHorizontal()) {
                glider.placeAgainstWall(clicked, face);
            }
            stack.shrink(1);
            level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, spawnPos);
        }
        return InteractionResult.CONSUME;
    }
}
