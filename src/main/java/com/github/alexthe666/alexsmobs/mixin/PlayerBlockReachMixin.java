package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemModArmor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerBlockReachMixin {

    @Inject(method = "blockActionRestricted", at = @At("RETURN"), cancellable = true)
    private void alexsmobs$kimonoBlockReach(Level level, BlockPos pos, GameType gameType, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            return;
        }
        float bonus = getKimonoReachBonus((Player) (Object) this);
        if (bonus <= 0.0F) {
            return;
        }
        Player self = (Player) (Object) this;
        double baseReach = gameType.isCreative() ? 5.0D : 4.5D;
        double maxDistSq = (baseReach + bonus) * (baseReach + bonus);
        if (self.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= maxDistSq) {
            cir.setReturnValue(false);
        }
    }

    private static float getKimonoReachBonus(Player player) {
        if (AMItemRegistry.BLOCK_REACH_ATTRIBUTE != null) {
            AttributeInstance attr = player.getAttribute(AMItemRegistry.BLOCK_REACH_ATTRIBUTE);
            if (attr != null) {
                float bonus = (float) (attr.getValue() - attr.getBaseValue());
                if (bonus > 0.0F) {
                    return bonus;
                }
            }
        }
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.getItem() instanceof ItemModArmor armor && armor.getArmorMaterialWrapper() == AMItemRegistry.KIMONO_MATERIAL) {
            return 2.0F;
        }
        return 0.0F;
    }
}
