package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {

    @Inject(method = "canEntityWalkOnPowderSnow", at = @At("HEAD"), cancellable = true)
    private static void alexsmobs$bisonFurPowderSnow(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!(entity instanceof LivingEntity living)) {
            return;
        }
        ItemStack boots = living.getItemBySlot(EquipmentSlot.FEET);
        if (boots.isEmpty() || boots.is(AMItemRegistry.ROADDRUNNER_BOOTS)) {
            return;
        }
        CompoundTag data = boots.getTag();
        if (data != null && data.getBoolean("BisonFur")) {
            cir.setReturnValue(true);
        }
    }
}
