package com.github.alexthe666.alexsmobs.mixin.client;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemModArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeReachMixin {

    @Inject(method = "getPickRange", at = @At("RETURN"), cancellable = true)
    private void alexsmobs$kimonoBlockReach(CallbackInfoReturnable<Float> cir) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        float bonus = getKimonoReachBonus(player);
        if (bonus > 0.0F) {
            cir.setReturnValue(cir.getReturnValue() + bonus);
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
