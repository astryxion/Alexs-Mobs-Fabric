package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerAttributesMixin {

    @Inject(method = "createAttributes", at = @At("RETURN"))
    private static void alexsmobs$addReachAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        if (AMItemRegistry.BLOCK_REACH_ATTRIBUTE != null) {
            builder.add(AMItemRegistry.BLOCK_REACH_ATTRIBUTE);
        }
        if (AMItemRegistry.ENTITY_REACH_ATTRIBUTE != null) {
            builder.add(AMItemRegistry.ENTITY_REACH_ATTRIBUTE);
        }
    }
}
