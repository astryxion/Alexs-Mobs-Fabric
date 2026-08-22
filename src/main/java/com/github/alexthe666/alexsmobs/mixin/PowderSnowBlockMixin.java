package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
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
        CustomData data = boots.get(DataComponents.CUSTOM_DATA);
        if (data != null && data.copyTag().getBoolean("BisonFur")) {
            cir.setReturnValue(true);
        }
    }
}
